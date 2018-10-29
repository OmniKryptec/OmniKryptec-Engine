package de.omnikryptec.resource.loader;

import java.io.File;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.resource.loadervpc.LoadingProgressCallback;
import de.omnikryptec.resource.loadervpc.ResourceProcessor;

public class Test {

	public static void main(String[] args) {
		ResourceProcessor p = new ResourceProcessor();
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
		System.out.println(new File("").exists());
		System.out.println(new AdvancedFile().exists());
		p.stage(new AdvancedFile("src/main/java"));
		p.processStaged(0.1f);
	}

}
