package de.omnikryptec.resource.loadervpc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.util.ExecutorsUtil;

public class ResourceProcessor {

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
    private Collection<ResourceLoader<?>> loadersThreadGroup;
    private Collection<ResourceLoader<?>> loadersMainThread;
    private List<ResourceLocation> staged;

    private ResourceProvider resourceProvider;
    private ResourceNameGenerator resourceNameGenerator;

    public ResourceProcessor() {
	this.callbacks = new ArrayList<>();
	this.staged = new ArrayList<>();
	this.loadersThreadGroup = new ArrayList<>();
	this.loadersMainThread = new ArrayList<>();
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

    public void processStaged(boolean override, float notifyProgress) {
	new Processor(override).processStaged(notifyProgress);
    }

    public void instantLoad(boolean override, AdvancedFile file) {
	new Processor(override).loadSimple(file);
    }

    private void addResource(Resource res, AdvancedFile file, AdvancedFile superfile, boolean override) {
	if (res != null) {
	    String name = resourceNameGenerator.genName(res, file, superfile);
	    resourceProvider.add(res, name, override);
	}
    }

    /*
     * Kind of messy, in here
     */
    private class Processor {
	private boolean notifyProgress;
	private int size;
	private int processed;
	private float quotient;
	private float lastquo;
	private float notifyfraction;
	private boolean override;
	private AdvancedFile superfile;

	private ExecutorService executorService = null;

	private Processor(boolean override) {
	    this.override = override;
	}

	private void resetExecutor() {
	    if (executorService != null) {
		throw new IllegalStateException("onetime-use only");
	    }
	    executorService = ExecutorsUtil.newFixedThreadPool(ExecutorsUtil.getAvailableProcessors());
	}

	private void processStaged(float notifyfraction) {
	    this.notifyfraction = notifyfraction;
	    this.notifyProgress = notifyfraction >= 0.0f;
	    Collections.sort(staged);
	    if (notifyProgress) {
		for (ResourceLocation top : staged) {
		    size = countFiles(top.getLocation(), size);
		}
		for (LoadingProgressCallback callback : callbacks) {
		    callback.onLoadingStart(size);
		}
	    }
	    resetExecutor();
	    for (ResourceLocation stagedFile : staged) {
		this.superfile = stagedFile.getLocation();
		processStagedIntern(stagedFile.getLocation());
	    }
	    ExecutorsUtil.shutdown(executorService, 1, TimeUnit.HOURS);
	    if (notifyProgress) {
		for (LoadingProgressCallback callback : callbacks) {
		    callback.onLoadingDone();
		}
	    }
	}

	private void processStagedIntern(AdvancedFile file) {
	    if (file.isDirectory()) {
		for (AdvancedFile subFile : file.listFiles()) {
		    processStagedIntern(subFile);
		}
	    } else {
		load(true, file);
		processed++;
		if (notifyProgress && ((quotient = (processed / (float) size)) - lastquo) >= notifyfraction) {
		    for (LoadingProgressCallback callback : callbacks) {
			callback.onProgressChange(processed);
		    }
		    lastquo = quotient;
		}
	    }
	}

	private void loadSimple(AdvancedFile file) {
	    boolean many = countFiles(file, 0) > 1;
	    if (many) {
		resetExecutor();
	    }
	    this.superfile = file;
	    loadSimpleIntern(many, file);
	    ExecutorsUtil.shutdown(executorService, 1, TimeUnit.HOURS);
	}

	private void loadSimpleIntern(boolean exec, AdvancedFile file) {
	    if (file.isDirectory()) {
		for (AdvancedFile subFile : file.listFiles()) {
		    loadSimpleIntern(exec, subFile);
		}
	    } else {
		load(exec, file);
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

	// TODO names, etc.
	private void load(boolean useExecutor, AdvancedFile file) {
	    Runnable r = () -> {
		for (ResourceLoader<?> loader : loadersThreadGroup) {
		    if (file.getName().matches(loader.getFileNameRegex())) {
			Resource res = loader.load(file);
			addResource(res, file, superfile, override);
		    }
		}
	    };
	    if (useExecutor) {
		executorService.submit(r);
	    } else {
		r.run();
	    }
	    for (ResourceLoader<?> loader : loadersMainThread) {
		if (file.getName().matches(loader.getFileNameRegex())) {
		    Resource res = loader.load(file);
		    addResource(res, file, superfile, override);
		}
	    }
	}
    }

}
