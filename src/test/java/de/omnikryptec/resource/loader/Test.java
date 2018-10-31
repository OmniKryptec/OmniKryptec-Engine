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
		return ".*java";
	    }

	    @Override
	    public boolean requiresMainThread() {
		return false;
	    }
	});
	p.addCallback(new LoadingProgressCallback() {

	    @Override
	    public void onLoadingStart(int max, int maxstages) {
		System.out.println("Max: "+max+" MaxS: "+maxstages);
	    }

	    @Override
	    public void onStageChange(AdvancedFile file, int localmax, int stagenumber) {
		System.out.println("Stagechange, lmax: "+localmax+ " S#: "+stagenumber);
	    }

	    @Override
	    public void onProgressChange(AdvancedFile f, int localprocessed) {
		System.out.println("P:"+localprocessed);
	    }

	    @Override
	    public void onLoadingDone() {
		System.out.println("Done!!!");
	    }

	    

	});
	p.stage(new AdvancedFile("src/main/java"));
	p.stage(new AdvancedFile("src/test/java"));
	p.processStaged(true);
	//System.out.println(p.getProvider().getAll(String.class).size());
	//System.out.println(p.getProvider().get(String.class, "de:omnikryptec:core:Updateable.java"));
    }

}
