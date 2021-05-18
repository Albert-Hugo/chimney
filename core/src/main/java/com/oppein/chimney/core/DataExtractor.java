package com.oppein.chimney.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ido
 * @date 2020/11/17 9:24
 */
public abstract class DataExtractor {
    private static final Logger logger = LoggerFactory.getLogger(DataExtractor.class);
    /**
     * 当前页面数
     */
    private AtomicInteger currentPageNo = new AtomicInteger(1);
    /**
     * 当前在运行的线程数
     */
    private AtomicInteger runingThread;


    DataExtractor setRuningThread(AtomicInteger runingThread) {
        this.runingThread = runingThread;
        return this;
    }

    /**
     * 从目标数据源获取需要转换的数据
     *
     * @param pageNo 当前页数
     * @return
     */
    public abstract Page getResultPage(final int pageNo);

    final public List extractDatas() {
        runingThread.incrementAndGet();
        final int currentPage = currentPageNo.getAndIncrement();
        logger.info("当前页数：{}", currentPage);
        long start = System.currentTimeMillis();
        List list;
        try {
            Page page = getResultPage(currentPage);
            list = page.getRecords();
        } catch (Exception e) {
            logger.error("页数{} 获取数据出错 ： {}", currentPage, e.getMessage());
            throw new IllegalStateException(e);
        }
        long end = System.currentTimeMillis();
        logger.info(" extractDatas 耗时 " + (end - start));

//        if (!list.isEmpty()) {
//            runingThread.incrementAndGet();
//        }

        return list;
    }
}
