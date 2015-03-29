/*
 * Copyright 1999-2012 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.elong.pb.newdda.net;

import com.elong.pb.newdda.route.RouteResultSetNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 单节点数据执行器
 */
public final class SingleNodeExecutor extends NodeExecutor {

    private final static Logger logger = LoggerFactory.getLogger(SingleNodeExecutor.class);

    public void execute(RouteResultSetNode node, FrontBackendSession session, String sql) {

    }

    @Override
    public void terminate() throws InterruptedException {

    }


}
