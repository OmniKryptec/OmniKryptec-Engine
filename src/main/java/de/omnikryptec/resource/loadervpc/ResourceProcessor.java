package de.omnikryptec.resource.loadervpc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.codemakers.io.file.AdvancedFile;

public abstract class ResourceProcessor {

	public static class ResourceLocation implements Comparable<ResourceLocation> {

		private int priority;
		private AdvancedFile loc;

		public ResourceLocation(int priority, AdvancedFile file) {
			this.priority = priority;
			this.loc = file;
		}

		public int getPriority() {
			return priority;
		}

		public AdvancedFile getLocation() {
			return loc;
		}

		@Override
		public int compareTo(ResourceLocation o) {
			return priority - o.priority;
		}
	}

	private Collection<LoadingProgressCallback> callbacks;
	private List<ResourceLocation> staged;

	private Processor processor;
	
	public ResourceProcessor() {
		this.callbacks = new ArrayList<>();
		this.staged = new ArrayList<>();
		this.processor = new Processor();
	}

	private void notifyProgressChangeCallbacks(int processed, int all) {
		for (LoadingProgressCallback callback : callbacks) {
			callback.onProgressChange(processed, all);
		}
	}

	public void addCallback(LoadingProgressCallback callback) {
		callbacks.add(callback);
	}

	public void stage(AdvancedFile file) {
		stage(new ResourceLocation(0, file));
	}

	public void stage(ResourceLocation file) {
		staged.add(file);
	}

	public void clearStaged() {
		staged.clear();
	}

	public void processStaged(float notifyProgress) {
		processor.processStaged(notifyProgress);
	}
	
	private class Processor {
		private boolean notifyProgress;
		private int size;
		private int processed;
		private float quotient;
		private float lastquo;
		private float notifyfraction;

		private void processStaged(float notifyfraction) {
			this.notifyfraction = notifyfraction;
			this.notifyProgress = notifyfraction >= 0.0f;
			this.size = 0;
			this.processed = 0;
			this.quotient = 0;
			this.lastquo = 0;
			Collections.sort(staged);
			if (notifyProgress) {
				for (ResourceLocation top : staged) {
					size = countFiles(top.getLocation(), size);
				}
				notifyProgressChangeCallbacks(0, size);
			}
			for (ResourceLocation stagedFile : staged) {
				processStagedIntern(stagedFile.getLocation());
			}
			if (notifyProgress) {
				notifyProgressChangeCallbacks(size, size);
			}
		}

		private void processStagedIntern(AdvancedFile file) {
			if (file.isDirectory()) {
				for (AdvancedFile subFile : file.listFiles()) {
					processStagedIntern(subFile);
				}
			} else {
				load(false, null, file);
				processed++;
				if (notifyProgress && ((quotient = (processed / (float) size)) - lastquo) >= notifyfraction) {
					notifyProgressChangeCallbacks(processed, size);
					lastquo = quotient;
				}
			}
		}

		private int countFiles(AdvancedFile file, int old) {
			if (file.isDirectory()) {
				List<AdvancedFile> filesHere = file.listFiles();
				for (AdvancedFile f : filesHere) {
					old = countFiles(f, old);
				}
			} else {
				old++;
			}
			return old;
		}
	}

	@Deprecated // TODO multithreaded, loader, names, etc.
	public abstract void load(boolean override, String nameprefix, AdvancedFile file);

	@Deprecated
	public abstract void clear();

}
