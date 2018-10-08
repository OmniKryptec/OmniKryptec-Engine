package de.omnikryptec.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ExecutorsUtil {

	private static Collection<ExecutorService> allExecutors = new ArrayList<>();

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
		allExecutors.add(executorService);
	}

	public static void unregister(ExecutorService executorService) {
		allExecutors.remove(executorService);
	}
	
	public static void shutdownNow(ExecutorService executorService) {
		executorService.shutdownNow();
		try {
			executorService.awaitTermination(1, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException("Awaiting termination failed of"+executorService, e);
		}
	}
	
	public static void shutdownNowAll() {
		for (ExecutorService executorService : allExecutors) {
			shutdownNow(executorService);
		}
	}
	
	public static int getAvailableProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}
}
