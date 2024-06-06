package com.compdfkit.tools.common.utils.threadpools;

import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class provides a thread pool implementation for executing multiple tasks concurrently.
 */
public class CThreadPoolUtils {

    /**
     * The number of available processors in the system.
     */
    private final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * The minimum number of threads to keep in the pool.
     */
    private final int CORE_POOL_SIZE = Math.max(1, CPU_COUNT - 1);

    /**
     * The maximum number of threads to allow in the pool.
     */
    private final int MAX_POOL_SIZE = (CPU_COUNT * 2) + 1;

    /**
     * The time to keep non-core threads alive in the pool.
     */
    private final int KEEP_ALIVE_TIME = 15;

    /**
     * The queue to hold tasks waiting to be executed.
     */
    private final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(64);

    private Handler handler = new Handler(Looper.getMainLooper());

    /**
     * The thread factory used to create new threads for the pool.
     */
    private final ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger integer = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ThreadPoolUtils thread:" + integer.getAndIncrement());
        }
    };

    /**
     * The actual thread pool executor instance.
     */
    public final ThreadPoolExecutor poolExecutor;

    /**
     * Private constructor to prevent external instantiation.
     */
    private CThreadPoolUtils() {
        ThreadPoolExecutor mThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue,
                threadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
        // Allow core threads to time out after being idle for a while.
        mThreadPoolExecutor.allowCoreThreadTimeOut(true);
        this.poolExecutor = mThreadPoolExecutor;
    }

    /**
     * The singleton holder class for the thread pool instance.
     */
    private final static class SingleTon {
        private final static CThreadPoolUtils instance = new CThreadPoolUtils();
    }

    /**
     * Returns the singleton instance of the ThreadPoolUtils class.
     *
     * @return The singleton instance of the ThreadPoolUtils class.
     */
    public static synchronized CThreadPoolUtils getInstance() {
        return SingleTon.instance;
    }

    /**
     * Executes a single command in the thread pool.
     *
     * @param command The command to execute.
     */
    public void executeMain(final Runnable command) {
        poolExecutor.execute(()-> handler.post(command));
    }

    public void executeIO(final Runnable command) {
        poolExecutor.execute(command);
    }


    /**
     * Executes a list of commands in the thread pool.
     *
     * @param commands The list of commands to execute.
     */
    public void executeMain(final List<Runnable> commands) {
        for (Runnable command : commands) {
            poolExecutor.execute(command);
        }
    }
}