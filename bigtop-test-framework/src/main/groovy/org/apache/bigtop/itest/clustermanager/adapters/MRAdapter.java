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
public interface MRAdapter {
  /**
   * Stops Map-Reduce service on namenode.
   */
  void stopMR(Host hostname);
  /**
   * Starts Map-Reduce service on namenode.
   */
  void startMR(Host hostname);
  /**
   * Restarts Map-Reduce service on namenode.
   */
  void restartMR(Host hostname);
  /**
  * Stops jobtracker on host if it holds jobtracker.
  */
  void stopJobTracker(Host hostname);
  /**
  * Starts jobtracker on host if it holds jobtracker.
  */
  void startJobTracker(Host hostname);
  /**
  * Restarts jobtracker on host if it holds jobtracker.
  */
  void restartJobTracker(Host hostname);
  /**
  * Stops tasktracker on host if it holds tasktracker.
  */
  void stopTaskTracker(Host hostname);
  /**
  * Starts tasktracker on host if it holds tasktracker.
  */
  void startTaskTracker(Host hostname);
  /**
  * Restarts tasktracker on host if it holds tasktracker.
  */
  void restartTaskTracker(Host hostname);
  
}
