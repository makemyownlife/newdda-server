package com.elong.pb.newdda.net;

import com.elong.pb.newdda.common.ExecutorUtil;
import com.elong.pb.newdda.common.NameableExecutor;
import com.elong.pb.newdda.config.SystemConfig;
import com.elong.pb.newdda.net.async.AsyncCommand;
import com.elong.pb.newdda.net.async.AsyncCommandFactory;
import com.elong.pb.newdda.packet.CommandPacket;
import com.elong.pb.newdda.packet.MysqlPacket;
import com.elong.pb.newdda.packet.factory.CommandPacketFactory;
import com.elong.pb.newdda.route.RouteResultSetNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 多数据节点执行器
 */
public final class MultiNodeExecutor extends NodeExecutor {

    private static final Logger logger = LoggerFactory.getLogger(MultiNodeExecutor.class);

    private static NameableExecutor MULTI_NODE_THREAD_POOL = ExecutorUtil.create("multiNodeThreadPool", SystemConfig.DEFAULT_PROCESSORS);

    private static final int MAX_PACKET_SIZE = 16 * 1024 * 1024;

    private static final int DEFAULT_EXECUTE_TIME_OUT = 20 * 1000;

    private AtomicBoolean isFail = new AtomicBoolean(false);

    private int unfinishedNodeCount;

    private final ReentrantLock lock = new ReentrantLock();

    //异步发送包后,需要解析
    private AsyncCommand asyncCommand;

    //用于超时设置 可以查看commonRpc 相关的内容看看是否是通过countdownlatch来配置的
    private CountDownLatch countDownLatch;

    public void execute(RouteResultSetNode[] nodes, FrontBackendSession session, String sql) {
        //初始化(相关的参数)
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            this.isFail.set(false);
            this.unfinishedNodeCount = nodes.length;
            this.countDownLatch = new CountDownLatch(nodes.length);
            this.asyncCommand = AsyncCommandFactory.createAsyncCommand(this, sql);
        } finally {
            lock.unlock();
        }
        //分析所有的链接 并且将所有的链接设置为正在执行的状态
        ConcurrentHashMap<RouteResultSetNode, BackendDdaChannel> target = session.getTarget();
        for (RouteResultSetNode rrn : nodes) {
            BackendDdaChannel backendDdaChannel = target.get(rrn);
            if (backendDdaChannel != null) {
                backendDdaChannel.setRunning(true);
                backendDdaChannel.setCurrentSession(session);
            }
        }
        //命令包
        CommandPacket commandPacket = null;
        try {
            commandPacket = CommandPacketFactory.createQueryCommand(sql, SystemConfig.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error("can't encoding sql :{}", sql);
        }
        //发送不同的命令到相关的数据
        for (RouteResultSetNode rrn : nodes) {
            final BackendDdaChannel backendDdaChannel = target.get(rrn);
            final CommandPacket temp = commandPacket;
            MULTI_NODE_THREAD_POOL.execute(new Runnable() {
                @Override
                public void run() {
                    backendDdaChannel.write(temp);
                }
            });
        }
        //期待是否会过期
        try {
            boolean executed = countDownLatch.await(DEFAULT_EXECUTE_TIME_OUT, TimeUnit.MILLISECONDS);
            if (!executed) {
                logger.error("sorry executed sql :{} time out ,please check out why ", sql);
                //过期则发送超时错误命令
            }else {
                //得到结果 并且返回给前端
                FrontDdaChannel frontDdaChannel = session.getFrontDdaChannel();
                this.asyncCommand.encodeForFront(frontDdaChannel);
                //正常释放资源 链接放入连接池
                session.release();
            }
        } catch (InterruptedException e) {
            logger.warn("multiNodeExecutor interrupted message would be {}", e.getMessage());
        }

    }

    @Override
    public void terminate() throws InterruptedException {

    }

    public void countDown() {
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }

    //解析从后端传来的mysql包
    @Override
    public void asyncMysqlPacket(BackendDdaChannel backendDdaChannel, MysqlPacket mysqlPacket) {
        asyncCommand.asyncMysqlPacket(backendDdaChannel, mysqlPacket);
    }


}
