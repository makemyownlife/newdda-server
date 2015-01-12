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
package com.elong.pb.newdda.config;


/**
 * 系统基础配置项
 *
 * @author xianmao.hexm 2011-1-11 下午02:14:04
 */
public final class SystemConfig {

    public static final String DEFAULT_CHARSET = "UTF-8";

    public static final int DEFAULT_PROCESSORS = Runtime.getRuntime().availableProcessors();

    public static final long DEFAULT_IDLE_TIMEOUT = 8 * 3600 * 1000L;

    public static final long DEFAULT_PROCESSOR_CHECK_PERIOD = 15 * 1000L;

    //默认最常的执行时间 若超过此时间则 超时返回错误
    public static final long DEFAULT_EXECUTE_TIME_OUT = 15 * 1000L;

}
