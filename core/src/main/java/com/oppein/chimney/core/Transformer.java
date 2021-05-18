package com.oppein.chimney.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据转换器
 *
 * @param <T> 源数据类型
 * @author : Ido
 * @date : 2020-11-17 16:13
 */
public abstract class Transformer<T> {
    private final static Logger logger = LoggerFactory.getLogger(Transformer.class);
    /**
     * 当前在运行的线程数
     */
    private AtomicInteger runingThread;
    /**
     * 已经迁移的数量
     */
    private AtomicInteger recordsNum;
    /**
     * 结果页数量
     */
    private int pageSize;
    private Semaphore semaphore;

    Transformer setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    Transformer setRecordsNum(AtomicInteger recordsNum) {
        this.recordsNum = recordsNum;
        return this;
    }

    Transformer setRuningThread(AtomicInteger runingThread) {
        this.runingThread = runingThread;
        return this;
    }

    Transformer setSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
        return this;
    }

    public void syncData(List<T> toBeTransformedData) {
        try {
            transformData(toBeTransformedData);
            recordsNum.addAndGet(toBeTransformedData.size());
        } catch (Exception e) {
            throw e;
        } finally {
            runingThread.decrementAndGet();
            semaphore.release();
        }

        logger.info("当前完成的迁移数量：{}", recordsNum.get());


    }

    /**
     * 数据转换
     *
     * @param toBeTransformedData
     */
    public abstract void transformData(List<T> toBeTransformedData);


}
