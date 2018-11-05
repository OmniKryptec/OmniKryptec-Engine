/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExecutorsUtil {

    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final Queue<ExecutorService> allExecutors = new ConcurrentLinkedQueue<>();
    private static final AtomicBoolean lock = new AtomicBoolean(false);

    public static ExecutorService newFixedThreadPool() {
        return newFixedThreadPool(AVAILABLE_PROCESSORS);
    }

    public static ExecutorService newFixedThreadPool(int nthreads) {
        ExecutorService service = Executors.newFixedThreadPool(nthreads);
        register(service);
        return service;
    }

    public static ExecutorService newFixedTHreadPool(int nthreads, ThreadFactory threadfactory) {
        ExecutorService service = Executors.newFixedThreadPool(nthreads, threadfactory);
        register(service);
        return service;
    }

    public static void register(ExecutorService executorService) {
        if (lock.get()) {
            throw new IllegalStateException("Currently shutdowning all");
        }
        allExecutors.add(executorService);
    }

    public static void unregister(ExecutorService executorService) {
        if (lock.get()) {
            throw new IllegalStateException("Currently shutdowning all");
        }
        allExecutors.remove(executorService);
    }

    public static void shutdownNow(ExecutorService executorService) {
        shutdown(executorService, 1, TimeUnit.MILLISECONDS);
    }

    public static void shutdown(ExecutorService executorService, long time, TimeUnit unit) {
        if (lock.get()) {
            throw new IllegalStateException("Already shutdowning all");
        }
        shutdownIntern(executorService, time, unit);
    }

    private static void shutdownIntern(ExecutorService executorService, long time, TimeUnit unit) {
        executorService.shutdownNow();
        allExecutors.remove(executorService);
        try {
            executorService.awaitTermination(time, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException("Awaiting termination failed:" + executorService, e);
        }
    }

    public static void shutdownNowAll() {
        if (lock.get()) {
            throw new IllegalStateException("Already shutdowning");
        }
        lock.set(true);
        try {
            while (!allExecutors.isEmpty()) {
                shutdownIntern(allExecutors.peek(), 1, TimeUnit.MILLISECONDS);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            lock.set(false);
        }
    }

}
