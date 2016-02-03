package com.elong.pb.newdda.net;

import com.elong.pb.newdda.handler.FrontAuthHandler;
import com.elong.pb.newdda.handler.FrontQueryHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 前端链接
 * Created by zhangyong on 15/7/23.
 */
public class FrontDdaChannel extends DdaChannel {

    private static final Logger logger  = LoggerFactory.getLogger(FrontDdaChannel.class);

    private final AcceptIdGenerator acceptIdGenerator = new AcceptIdGenerator();

    private Channel channel;

    //对于client来说 配置的数据源 也就是相当于后端的 schemaId 参见 schema.xml
    private String dataSource;

    //前端编号
    private Long id;

    private byte[] seed;

    private volatile boolean authenticated;

    private FrontAuthHandler frontAuthHandler;

    private FrontQueryHandler frontQueryHandler;

    private FrontBackendSession frontBackendSession;

    public FrontDdaChannel(Channel channel){
        this.channel = channel;
        this.id = acceptIdGenerator.getId();
        this.authenticated = false;
        this.frontAuthHandler = new FrontAuthHandler(this);
        this.frontQueryHandler = new FrontQueryHandler(this);
        this.frontBackendSession = new FrontBackendSession(this);
    }

    @Override
    public void write(Object message) {
        this.channel.writeAndFlush(message).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    logger.error(
                            "frontChannel write error: " + channel.remoteAddress(),
                            future.cause());
                }
            }
        });;
    }

    /**
     * 前端连接ID生成器
     *
     * @author xianmao.hexm
     */
    private static class AcceptIdGenerator {
        private static final long MAX_VALUE = 0xffffffffL;
        private long acceptId = 0L;
        private final Object lock = new Object();
        private long getId() {
            synchronized (lock) {
                if (acceptId >= MAX_VALUE) {
                    acceptId = 0L;
                }
                return ++acceptId;
            }
        }
    }

    //===============================================get set method =======================================================
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public byte[] getSeed() {
        return seed;
    }

    public void setSeed(byte[] seed) {
        this.seed = seed;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public FrontAuthHandler getFrontAuthHandler() {
        return frontAuthHandler;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public FrontQueryHandler getFrontQueryHandler() {
        return frontQueryHandler;
    }

    public void setFrontQueryHandler(FrontQueryHandler frontQueryHandler) {
        this.frontQueryHandler = frontQueryHandler;
    }

    public FrontBackendSession getFrontBackendSession() {
        return frontBackendSession;
    }
}
