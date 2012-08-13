package org.apache.bigtop.itest.clustermanager.adapters;
/**
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

import java.io.IOException;

import org.apache.bigtop.itest.clustermanager.builders.Host;

/**
 * To have the ClusterManager API compatible with all versions of Hadoop,
 * all non cross-version compatible calls are encapsulated within this interface.
 */
public interface HDFSAdapter {
  /**
   * Stops HDFS service on host.
   */
  void stopHDFS(Host hostname);
  /**
   * Starts HDFS service on host.
   */
  void startHDFS(Host hostname);
  /**
   * Restarts HDFS service on host.
   */
  void restartHDFS(Host hostname);
  /**
  * Starts namenode on host if it holds a namenode.
  */
  void startNameNode(Host hostname); 
  /**
  * Stops namenode on host if it holds a namenode.
  */
  void stopNameNode(Host hostname);
  /**
  * Restarts namenode on host if it holds a namenode.
  */
  void restartNameNode(Host hostname);
  /**
   * Starts datanode on host if it holds a namenode.
   */
  void startDataNode(Host hostname); 
  /**
  * Stops datanode on host if it holds a namenode.
  */
  void stopDataNode(Host hostname);
   /**
   * Restarts datanode on host if it holds a namenode.
   */
  void restartDataNode(Host hostname);
}
