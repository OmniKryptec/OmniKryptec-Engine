package de.omnikryptec.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExecutorsUtil {

    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final Queue<ExecutorService> allExecutors = new ConcurrentLinkedQueue<>();
    private static final AtomicBoolean lock = new AtomicBoolean(false);

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

    public static int getAvailableProcessors() {
	return AVAILABLE_PROCESSORS;
    }
}
