package com.savbill.revenuemanagement.core.service.postpaid;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public abstract class PostpaidInvoiceThread {

    private ExecutorService invoiceService;

    @Value("${thread.min: 50}")
    private String minThreads;

    @Value("${thread.max: 50}")
    private String maxThreads;

    private int threadBenchTime = 0;

    public String getMinThreads() {
        return minThreads;
    }

    public void setMinThreads(String minThreads) {
        this.minThreads = minThreads;
    }

    public String getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(String maxThreads) {
        this.maxThreads = maxThreads;
    }

    public int getThreadBenchTime() {
        return threadBenchTime;
    }

    public void setThreadBenchTime(int threadBenchTime) {
        this.threadBenchTime = threadBenchTime;
    }

    public ExecutorService getInvoicePool() {

        Integer minThread=Integer.parseInt(minThreads);
        Integer maxThread=Integer.parseInt(maxThreads);
        if(invoiceService ==null) {
            System.out.println("AUTH POOL Initialized: min:" + minThreads + ",Max:"+ maxThreads);
            invoiceService = new ThreadPoolExecutor(minThread, maxThread, threadBenchTime, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("AUTHPOOL-%d").build());
        }
        return invoiceService;
    }
}
