package org.apache.bigtop.itest.hdfstests;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.*;
import org.apache.bigtop.itest.JarContent;
import org.apache.bigtop.itest.clustermanager.builders.ClusterManager;
import org.apache.bigtop.itest.clustermanager.builders.ClusterManagerFactory;
import org.apache.bigtop.itest.clustermanager.builders.Host;
import org.apache.bigtop.itest.shell.Shell;
import org.apache.hadoop.conf.Configuration;



public class TestHA_MR_Failover {
 
  private static Shell sh = new Shell("/bin/bash -s");
  private static final String HADOOP_HOME = System.getenv("HADOOP_HOME");
  private static final String HADOOP_CONF_DIR = System.getenv("HADOOP_CONF_DIR");
  private static long wait_time = 1;
  static boolean noActiveNN = false;
  public static ClusterManager myManager = new ClusterManagerFactory().buildContext("versionA");

  static {
    assertNotNull("HADOOP_HOME has to be set to run this test",
        HADOOP_HOME);
    assertNotNull("HADOOP_CONF_DIR has to be set to run this test",
        HADOOP_CONF_DIR);
  }

  @Test
  public void testMRJobFailovers() throws InterruptedException { 
	  
    HA_MR_FailoverHelperThread mapreduce = new HA_MR_FailoverHelperThread("one");
    // start MR sequence
    mapreduce.t.start();

    //repeated manual failover
    while (mapreduce.done != true) {
  	  Host active = myManager.getActiveNN();
  	  myManager.killDaemon("namenode", active);
  	  noActiveNN = true;
  	  myManager.failover();
  	  noActiveNN = false;
  	  myManager.startHDFS(active);
  	  myManager.waitUntilLeftSafemode();
  	  myManager.failover();
      Thread.sleep(wait_time); 
    }

   assertTrue("teragen failed", !mapreduce.genFail);
   assertTrue("terasort failed", !mapreduce.sortFail);
   assertTrue("teravalidate failed", !mapreduce.validateFail);
   assertTrue(mapreduce.exceptionString, !mapreduce.exception);
      
  }
} 



