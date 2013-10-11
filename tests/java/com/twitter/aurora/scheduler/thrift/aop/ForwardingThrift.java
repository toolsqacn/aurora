/*
 * Copyright 2013 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitter.aurora.scheduler.thrift.aop;

import java.util.Set;

import org.apache.thrift.TException;

import com.twitter.aurora.gen.AuroraAdmin;
import com.twitter.aurora.gen.Hosts;
import com.twitter.aurora.gen.JobConfiguration;
import com.twitter.aurora.gen.JobKey;
import com.twitter.aurora.gen.Lock;
import com.twitter.aurora.gen.LockKey;
import com.twitter.aurora.gen.LockValidation;
import com.twitter.aurora.gen.Quota;
import com.twitter.aurora.gen.Response;
import com.twitter.aurora.gen.RewriteConfigsRequest;
import com.twitter.aurora.gen.ScheduleStatus;
import com.twitter.aurora.gen.SessionKey;
import com.twitter.aurora.gen.TaskQuery;
import com.twitter.aurora.gen.UpdateResult;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A forwarding scheduler controller to make it easy to override specific behavior in an
 * implementation class.
 */
abstract class ForwardingThrift implements AuroraAdmin.Iface {

  private final AuroraAdmin.Iface delegate;

  ForwardingThrift(AuroraAdmin.Iface delegate) {
    this.delegate = checkNotNull(delegate);
  }

  @Override
  public Response setQuota(String ownerRole, Quota quota, SessionKey session)
      throws TException {

    return delegate.setQuota(ownerRole, quota, session);
  }

  @Override
  public Response forceTaskState(
      String taskId,
      ScheduleStatus status,
      SessionKey session) throws TException {

    return delegate.forceTaskState(taskId, status, session);
  }

  @Override
  public Response performBackup(SessionKey session) throws TException {
    return delegate.performBackup(session);
  }

  @Override
  public Response listBackups(SessionKey session) throws TException {
    return delegate.listBackups(session);
  }

  @Override
  public Response stageRecovery(String backupId, SessionKey session)
      throws TException {

    return delegate.stageRecovery(backupId, session);
  }

  @Override
  public Response queryRecovery(TaskQuery query, SessionKey session)
      throws TException {

    return delegate.queryRecovery(query, session);
  }

  @Override
  public Response deleteRecoveryTasks(TaskQuery query, SessionKey session)
      throws TException {

    return delegate.deleteRecoveryTasks(query, session);
  }

  @Override
  public Response commitRecovery(SessionKey session) throws TException {
    return delegate.commitRecovery(session);
  }

  @Override
  public Response unloadRecovery(SessionKey session) throws TException {
    return delegate.unloadRecovery(session);
  }

  @Override
  public Response createJob(JobConfiguration description, SessionKey session)
      throws TException {

    return delegate.createJob(description, session);
  }

  @Override
  public Response populateJobConfig(JobConfiguration description) throws TException {
    return delegate.populateJobConfig(description);
  }

  @Override
  public Response startCronJob(JobKey job, SessionKey session) throws TException {
    return delegate.startCronJob(job, session);
  }

  @Override
  public Response startUpdate(JobConfiguration updatedConfig, SessionKey session)
      throws TException {

    return delegate.startUpdate(updatedConfig, session);
  }

  @Override
  public Response updateShards(
      JobKey job,
      Set<Integer> shardIds,
      String updateToken,
      SessionKey session) throws TException {

    return delegate.updateShards(job, shardIds, updateToken, session);
  }

  @Override
  public Response rollbackShards(
      JobKey job,
      Set<Integer> shardIds,
      String updateToken,
      SessionKey session) throws TException {

    return delegate.rollbackShards(job, shardIds, updateToken, session);
  }

  @Override
  public Response finishUpdate(
      JobKey job,
      UpdateResult updateResult,
      String updateToken,
      SessionKey session) throws TException {

    return delegate.finishUpdate(job, updateResult, updateToken, session);
  }

  @Override
  public Response restartShards(
      JobKey job,
      Set<Integer> shardIds,
      SessionKey session) throws TException {

    return delegate.restartShards(job, shardIds, session);
  }

  @Override
  public Response getTasksStatus(TaskQuery query) throws TException {
    return delegate.getTasksStatus(query);
  }

  @Override
  public Response getJobs(String ownerRole) throws TException {
    return delegate.getJobs(ownerRole);
  }

  @Override
  public Response killTasks(TaskQuery query, SessionKey session) throws TException {
    return delegate.killTasks(query, session);
  }

  @Override
  public Response getQuota(String ownerRole) throws TException {
    return delegate.getQuota(ownerRole);
  }

  @Override
  public Response startMaintenance(Hosts hosts, SessionKey session)
      throws TException {

    return delegate.startMaintenance(hosts, session);
  }

  @Override
  public Response drainHosts(Hosts hosts, SessionKey session) throws TException {
    return delegate.drainHosts(hosts, session);
  }

  @Override
  public Response maintenanceStatus(Hosts hosts, SessionKey session)
      throws TException {

    return delegate.maintenanceStatus(hosts, session);
  }

  @Override
  public Response endMaintenance(Hosts hosts, SessionKey session) throws TException {
    return delegate.endMaintenance(hosts, session);
  }

  @Override
  public Response getJobUpdates(SessionKey session) throws TException {
    return delegate.getJobUpdates(session);
  }

  @Override
  public Response snapshot(SessionKey session) throws TException {
    return delegate.snapshot(session);
  }

  @Override
  public Response rewriteConfigs(RewriteConfigsRequest request, SessionKey session)
      throws TException {

    return delegate.rewriteConfigs(request, session);
  }

  @Override
  public Response getVersion() throws TException {
    return delegate.getVersion();
  }

  @Override
  public Response acquireLock(LockKey lockKey, SessionKey session) throws TException {
    return delegate.acquireLock(lockKey, session);
  }

  @Override
  public Response releaseLock(Lock lock, LockValidation validation, SessionKey session)
      throws TException {

    return delegate.releaseLock(lock, validation, session);
  }
}
