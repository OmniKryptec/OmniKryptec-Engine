/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExecutorsUtil {
    
    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final Queue<ExecutorService> allExecutors = new ConcurrentLinkedQueue<>();
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    
    private static final Logger logger = Logger.getLogger(ExecutorsUtil.class);
    
    public static final int getServiceInUseCount() {
        return allExecutors.size();
    }
    
    public static ExecutorService newFixedThreadPool() {
        return newFixedThreadPool(AVAILABLE_PROCESSORS);
    }
    
    public static ExecutorService newFixedThreadPool(final int nthreads) {
        final ExecutorService service = Executors.newFixedThreadPool(nthreads);
        register(service);
        return service;
    }
    
    public static ExecutorService newFixedTHreadPool(final int nthreads, final ThreadFactory threadfactory) {
        final ExecutorService service = Executors.newFixedThreadPool(nthreads, threadfactory);
        register(service);
        return service;
    }
    
    public static void register(final ExecutorService executorService) {
        if (lock.get()) {
            throw new IllegalStateException("Currently shutdowning all");
        }
        allExecutors.add(executorService);
    }
    
    public static void unregister(final ExecutorService executorService) {
        if (lock.get()) {
            throw new IllegalStateException("Currently shutdowning all");
        }
        allExecutors.remove(executorService);
    }
    
    public static void shutdownNow(final ExecutorService executorService) {
        shutdown(executorService, 1, TimeUnit.MILLISECONDS, true);
    }
    
    public static void shutdown(final ExecutorService executorService, final long time, final TimeUnit unit,
            final boolean now) {
        if (lock.get()) {
            throw new IllegalStateException("Already shutting down all");
        }
        shutdownIntern(executorService, time, unit, now);
    }
    
    private static void shutdownIntern(final ExecutorService executorService, final long time, final TimeUnit unit,
            final boolean now) {
        if (now) {
            executorService.shutdownNow();
        } else {
            executorService.shutdown();
        }
        allExecutors.remove(executorService);
        try {
            executorService.awaitTermination(time, unit);
        } catch (final InterruptedException e) {
            throw new RuntimeException("Awaiting termination failed:" + executorService, e);
        }
    }
    
    public static void shutdownNowAll() {
        if (allExecutors.isEmpty()) {
            logger.info("There are no running executors");
            return;
        }
        if (lock.get()) {
            throw new IllegalStateException("Already shutting down all");
        }
        lock.set(true);
        logger.info("Shutting down all executors...");
        try {
            while (!allExecutors.isEmpty()) {
                shutdownIntern(allExecutors.peek(), 1, TimeUnit.MILLISECONDS, true);
            }
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            lock.set(false);
        }
    }
    
}
