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
package org.apache.ratis.client.api;

import java.util.concurrent.CompletableFuture;
import org.apache.ratis.proto.RaftProtos.ReplicationLevel;
import org.apache.ratis.protocol.Message;
import org.apache.ratis.protocol.RaftClientReply;
import org.apache.ratis.protocol.RaftPeerId;

/**
 * Asynchronous API to support operations
 * such as sending message, read-message, stale-read-message and watch-request.
 *
 * Note that this API and {@link BlockingApi} support the same set of operations.
 */
public interface AsyncApi {
  /**
   * Send the given message asynchronously to the raft service.
   * The message may change the state of the service.
   * For readonly messages, use {@link #sendReadOnly(Message)} instead.
   *
   * @param message The request message.
   * @return a future of the reply.
   */
  CompletableFuture<RaftClientReply> send(Message message);

  /**
   * Send the given readonly message asynchronously to the raft service.
   *
   * @param message The request message.
   * @return a future of the reply.
   */
  CompletableFuture<RaftClientReply> sendReadOnly(Message message);

  /**
   * Send the given stale-read message asynchronously to the given server (not the raft service).
   * If the server commit index is larger than or equal to the given min-index, the request will be processed.
   * Otherwise, the server returns a {@link org.apache.ratis.protocol.exceptions.StaleReadException}.
   *
   * @param message The request message.
   * @param minIndex The minimum log index that the server log must have already committed.
   * @param server The target server
   * @return a future of the reply.
   */
  CompletableFuture<RaftClientReply> sendStaleRead(Message message, long minIndex, RaftPeerId server);

  /**
   * Watch the given index asynchronously to satisfy the given replication level.
   *
   * @param index The log index to be watched.
   * @param replication The replication level required.
   * @return a future of the reply.
   *         When {@link RaftClientReply#isSuccess()} == true,
   *         the reply index (i.e. {@link RaftClientReply#getLogIndex()}) is the log index satisfying the request,
   *         where reply index >= watch index.
   */
  CompletableFuture<RaftClientReply> watch(long index, ReplicationLevel replication);
}
