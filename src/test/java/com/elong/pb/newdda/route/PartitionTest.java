package com.elong.pb.newdda.route;

import com.elong.pb.newdda.route.util.PartitionUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by zhangyong on 15/6/4.
 */
public class PartitionTest {

    @Test
    public void mypu(){
        int[] count = new int[] { 1, 1 };
        int[] length = new int[] { 512, 512 };
        PartitionUtil pu = new PartitionUtil(count, length);

        // 下面代码演示分别以offerId字段或memberId字段根据上述分区策略拆分的分配结果
        int DEFAULT_STR_HEAD_LEN = 8; // cobar默认会配置为此值
        long offerId = 12345;
        String memberId = "qiushuo";

        // 若根据offerId分配，partNo1将等于0，即按照上述分区策略，offerId为12345时将会被分配到partition0中
        int partNo1 = pu.partition(offerId);

        // 若根据memberId分配，partNo2将等于2，即按照上述分区策略，memberId为qiushuo时将会被分到partition2中
        int partNo2 = pu.partition(memberId, 0, DEFAULT_STR_HEAD_LEN);

        Assert.assertEquals(0, partNo1);
        Assert.assertEquals(2, partNo2);
    }

}
