package com.savbill.revenuemanagement.tasks;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

/**
 * <h2>SearchMainThread is thread main class.</h2>
 * <p>
 * SearchMainThread is the abstract class and its implements runnable interface.
 *
 * @author Deep Sherathiya
 * @version 2.0
 * @since 2.0
 */
public abstract class CustomMainThread implements Runnable {

    /**
     * <h3>process method is used to process index based search request on given parameter.</h3>
     *
     * @throws ParseException if error occurred while parsing data.
     * @throws ExecutionException if error occurred while getting element from future.
     * @throws InterruptedException if error occurred while getting element from future.
     * @throws IOException if error occurred while writing result in json file.
     */
    public abstract void process() throws ParseException, ExecutionException, InterruptedException, IOException;

    /**
     * <h3>getPriority method is used to get task priority.</h3>
     * @return task priority.
     */
    public abstract int getPriority();

}
