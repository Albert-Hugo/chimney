package com.oppein.chimney.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : Ido
 * @description : 数据同步 work task
 * @date : 2020-11-17 16:13
 **/
public class SyncDataTask {
    private static final Logger logger = LoggerFactory.getLogger(SyncDataTask.class);

    private AtomicBoolean done = new AtomicBoolean(false);
    /**
     * 数据转换器
     */
    private Transformer transformer;
    /**
     * 源数据获取器
     */
    private DataExtractor dataExtractor;
    /**
     * 任务名
     */
    private String taskName;
    /**
     * 默认线程数
     */
    private static final int DEFAULT_THREAD_NUM = 20;
    /**
     * 信号量，控制同时运行的线程数
     */
    private final Semaphore semaphore;
    /**
     * 当前启动的线程数
     */
    private AtomicInteger runingThread = new AtomicInteger(0);
    /**
     * 当前已经迁移完成的记录数
     */
    private AtomicInteger recordsNum = new AtomicInteger(0);

    private ThreadPoolExecutor executor;

    public SyncDataTask(Transformer transformer, DataExtractor dataExtractor, String taskName) {
        this(transformer, dataExtractor, taskName, DEFAULT_THREAD_NUM);
    }

    public SyncDataTask(Transformer transformer, DataExtractor dataExtractor, String taskName, int threadNum) {
        semaphore = new Semaphore(threadNum);

        this.transformer = transformer;
        this.transformer.setRuningThread(runingThread);
        this.transformer.setSemaphore(semaphore);
        this.transformer.setRecordsNum(recordsNum);
        this.dataExtractor = dataExtractor;
        this.dataExtractor.setRuningThread(runingThread);
        this.taskName = taskName;

        executor = new ThreadPoolExecutor(threadNum, 100, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "worker thread");
                t.setUncaughtExceptionHandler((thread, e) -> {
                    logger.error(e.getMessage(), e);
                });
                return t;
            }
        });

    }


    /**
     * 开启同步
     */
    final public void startSync() {
        long start = System.currentTimeMillis();
        while (!done.get()) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
            executor.execute(() -> {

                List targetDatas = dataExtractor.extractDatas();

                if (targetDatas == null || targetDatas.size() == 0) {
                    done.set(true);
                    semaphore.release();
                    runingThread.decrementAndGet();
                    return;
                }
                transformer.setPageSize(targetDatas.size());
                transformer.syncData(targetDatas);
            });

        }

        while (runingThread.get() > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }

        long end = System.currentTimeMillis();

        logger.info(taskName + " ----------完成-----------, 耗时 ： " + (end - start) / 1000 + " 秒");
        executor.shutdown();

    }

}
