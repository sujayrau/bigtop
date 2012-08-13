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
public interface ClusterAdapter {
  /**
   * Starts specified daemon on specified host.
   */
 void startDaemon(String daemon, Host hostname);
  /**
   * Stops specified daemon on specified host.
   */
 void stopDaemon(String daemon, Host hostname);
  /**
   * Restarts specified daemon on specified host.
   */
 void restartDaemon(String daemon, Host hostname);

}
