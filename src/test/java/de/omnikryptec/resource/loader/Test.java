package de.omnikryptec.resource.loader;

import java.io.File;
import java.util.stream.Collectors;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.resource.loadervpc.LoadingProgressCallback;
import de.omnikryptec.resource.loadervpc.ResourceLoader;
import de.omnikryptec.resource.loadervpc.ResourceProcessor;

public class Test {


    public static void main(String[] args) {
	ResourceProcessor p = new ResourceProcessor();
	p.addLoader(new ResourceLoader<String>() {

	    @Override
	    public String load(AdvancedFile file) throws Exception {
		return file.createBufferedReader().lines().collect(Collectors.joining("\n"));
	    }

	    @Override
	    public String getFileNameRegex() {
		return ".*er\\.java";
	    }
	}, true);
	p.addCallback(new LoadingProgressCallback() {

	    private int max;

	    @Override
	    public void onLoadingStart(int max) {
		this.max = max;
	    }

	    @Override
	    public void onProgressChange(int processed) {
		System.out.println(processed + "/" + max);
	    }

	    @Override
	    public void onLoadingDone() {
	    }

	});
	p.stage(new AdvancedFile("src/main/java"));
	p.processStaged(true, 0.1f);
	System.out.println(p.getProvider().getAll(String.class).size());
	System.out.println(p.getProvider().get(String.class, "de:omnikryptec:core:Updateable.java"));
    }

}
