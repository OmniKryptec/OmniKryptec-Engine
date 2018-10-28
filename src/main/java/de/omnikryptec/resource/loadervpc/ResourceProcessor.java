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

	public ResourceProcessor() {
		this.callbacks = new ArrayList<>();
		this.staged = new ArrayList<>();
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

	public void processStaged(boolean notifyProgress) {
		int size = 0;
		int processed = 0;
		float quotient = 0;
		float lastquo = 0;
		Collections.sort(staged);
		if (notifyProgress) {
			for (ResourceLocation top : staged) {
				size = countFiles(top.getLocation(), size);
			}
			notifyProgressChangeCallbacks(0, size);
		}
		for (ResourceLocation stagedFile : staged) {
			if (stagedFile.getLocation().isDirectory()) {
				for (AdvancedFile subFile : stagedFile.getLocation().listFiles()) {
					load(false, null, subFile);
					processed++;
					if (notifyProgress && ((quotient = (processed / (float) size)) - lastquo) >= 1f) {
						notifyProgressChangeCallbacks(processed, size);
						lastquo = quotient;
					}
				}
			} else {
				load(false, null, stagedFile.getLocation());
				processed++;
				if (notifyProgress && ((quotient = (processed / (float) size)) - lastquo) >= 1f) {
					notifyProgressChangeCallbacks(processed, size);
					lastquo = quotient;
				}
			}
		}
		if (notifyProgress) {
			notifyProgressChangeCallbacks(size, size);
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

	@Deprecated // TODO multithreaded, loader, names, etc.
	public abstract void load(boolean override, String nameprefix, AdvancedFile file);

	@Deprecated
	public abstract void clear();

}
