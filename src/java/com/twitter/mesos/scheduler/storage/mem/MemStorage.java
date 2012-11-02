package com.twitter.mesos.scheduler.storage.mem;

import java.util.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

import com.twitter.mesos.scheduler.storage.AttributeStore;
import com.twitter.mesos.scheduler.storage.AttributeStore.AttributeStoreImpl;
import com.twitter.mesos.scheduler.storage.JobStore;
import com.twitter.mesos.scheduler.storage.LockManager;
import com.twitter.mesos.scheduler.storage.QuotaStore;
import com.twitter.mesos.scheduler.storage.SchedulerStore;
import com.twitter.mesos.scheduler.storage.Storage;
import com.twitter.mesos.scheduler.storage.Storage.MutateWork.NoResult.Quiet;
import com.twitter.mesos.scheduler.storage.TaskStore;
import com.twitter.mesos.scheduler.storage.Transactional;
import com.twitter.mesos.scheduler.storage.UpdateStore;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A storage implementation comprised of individual in-memory store implementations.
 */
public class MemStorage implements Storage {
  private static final Logger LOG = Logger.getLogger(MemStorage.class.getName());

  private final MutableStoreProvider storeProvider;
  private final LockManager lockManager = new LockManager();
  private final Iterable<Transactional> transactionals;

  @Inject
  MemStorage(
      final SchedulerStore.Mutable.Transactioned schedulerStore,
      final JobStore.Mutable.Transactioned jobStore,
      final TaskStore.Mutable.Transactioned taskStore,
      final UpdateStore.Mutable.Transactioned updateStore,
      final QuotaStore.Mutable.Transactioned quotaStore,
      final AttributeStore.Mutable attributeStore) {

    storeProvider = new MutableStoreProvider() {
      @Override public SchedulerStore.Mutable getSchedulerStore() {
        return schedulerStore;
      }

      @Override public JobStore.Mutable getJobStore() {
        return jobStore;
      }

      @Override public TaskStore.Mutable getTaskStore() {
        return taskStore;
      }

      @Override public UpdateStore.Mutable getUpdateStore() {
        return updateStore;
      }

      @Override public QuotaStore.Mutable getQuotaStore() {
        return quotaStore;
      }

      @Override public AttributeStore.Mutable getAttributeStore() {
        return attributeStore;
      }
    };
    transactionals = ImmutableList.of(
        schedulerStore,
        jobStore,
        taskStore,
        updateStore,
        quotaStore);
  }

  /**
   * Creates a new empty in-memory storage for use in testing.
   */
  @VisibleForTesting
  public static MemStorage newEmptyStorage() {
    return new MemStorage(
        new MemSchedulerStore(),
        new MemJobStore(),
        new MemTaskStore(),
        new MemUpdateStore(),
        new MemQuotaStore(),
        new AttributeStoreImpl());
  }

  @Override
  public void prepare() {
    // No-op.
  }

  @Override
  public void start(final Quiet initializationLogic) {
    checkNotNull(initializationLogic);

    doInWriteTransaction(initializationLogic);
    LOG.info("Applied initialization logic.");
  }

  @Override
  public <T, E extends Exception> T doInTransaction(Work<T, E> work) throws StorageException, E {
    checkNotNull(work);

    lockManager.readLock();
    try {
      return work.apply(storeProvider);
    } finally {
      lockManager.readUnlock();
    }
  }

  private void commit() {
    for (Transactional transactional : transactionals) {
      transactional.commit();
    }
  }

  private void rollback() {
    for (Transactional transactional : transactionals) {
      transactional.rollback();
    }
  }

  @Override
  public <T, E extends Exception> T doInWriteTransaction(MutateWork<T, E> work)
      throws StorageException, E {

    checkNotNull(work);

    boolean committed = false;
    boolean topLevelTransaction = lockManager.writeLock();
    try {
      T result =  work.apply(storeProvider);
      if (topLevelTransaction) {
        commit();
        committed = true;
      }
      return result;
    } finally {
      if (topLevelTransaction && !committed) {
        rollback();
      }
      lockManager.writeUnlock();
    }
  }

  @Override
  public void stop() {
    // No-op.
  }
}
