package com.elong.pb.newdda.test.jdbc.config;

import com.elong.pb.newdda.config.DdaCapability;
import org.junit.Test;

/**
 * Created by zhangyong on 14/12/23.
 *
 */
public class DdaCapabilityTest {

    @Test
    public void testClient(){
        DdaCapability.getClientFlags();
    }

}
