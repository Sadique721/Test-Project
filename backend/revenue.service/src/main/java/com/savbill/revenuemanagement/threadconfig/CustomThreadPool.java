package com.savbill.revenuemanagement.threadconfig;

import liquibase.pro.packaged.T;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

public class CustomThreadPool {

    private final ThreadPoolExecutor executor;

    /**
     * Creates a new instance of the CustomThreadPool class.
     *
     * @param poolSize the number of threads to keep in the pool, even if they are idle.
     * @param maxPoolSize the maximum number of threads to allow in the pool.
     * @param keepAliveTime the maximum time that excess idle threads will wait for new tasks before terminating.
     * @param unit the time unit for the keepAliveTime argument.
     * @param threadNamePrefix the prefix to use for the thread names.
     */
    public CustomThreadPool(int poolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit, String threadNamePrefix) {
        // Validate the input parameters
        if (poolSize <= 0 || maxPoolSize <= 0 || maxPoolSize < poolSize || keepAliveTime < 0) {
            throw new IllegalArgumentException("Invalid input parameters.");
        }
        if (threadNamePrefix == null || threadNamePrefix.isEmpty()) {
            throw new IllegalArgumentException("Thread name prefix cannot be null or empty.");
        }

        // Create a thread factory with the specified thread name prefix
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder()
                .namingPattern(threadNamePrefix + "-%d")
                .daemon(true).priority(Thread.MAX_PRIORITY)
                .build();

        // Create a blocking queue for holding tasks
//        PriorityBlockingQueue<Runnable> taskQueue = new PriorityBlockingQueue<>(10, new PriorityComparator());


        // Create a thread pool executor with the specified configuration
        executor = new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime, unit, new LinkedBlockingQueue<>(), threadFactory);
    }

    /**
     * Submits a task to the thread pool for execution.
     *
     * @param task the task to submit to the thread pool.
     * @return result of submitted task once it finished
     */
    public Future<?> submitTask(Runnable task) {
        // Validate the input parameter
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }

        // Submit the task to the thread pool
        return executor.submit(task);
    }

    public <T> Future<T> submitTask(Callable<T> task) {
        // Validate the input parameter
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }

        // Submit the task to the thread pool
        return executor.submit(task);
    }

    /**
     * Submits a task to the thread pool for execution.
     *
     * @param task the task to submit to the thread pool.
     */
    public void executeTask(Runnable task) {
        // Validate the input parameter
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }

        // Submit the task to the thread pool
        executor.execute(task);
    }

    /**
     * Shuts down the thread pool.
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Custom thread factory that creates threads with the specified name prefix.
     */
    private static class CustomThreadFactory implements ThreadFactory {
        private final String threadNamePrefix;
        private int threadCount;

        public CustomThreadFactory(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
            this.threadCount = 0;
        }

        @Override
        public Thread newThread(Runnable r) {
            threadCount++;
            return new Thread(r, threadNamePrefix + "-" + threadCount);
        }
    }
}
