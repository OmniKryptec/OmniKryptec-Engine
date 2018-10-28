package de.omnikryptec.resource.loader;

import java.io.File;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.resource.loadervpc.LoadingProgressCallback;
import de.omnikryptec.resource.loadervpc.ResourceProcessor;

public class Test {

	public static void main(String[] args) {
		ResourceProcessor p = new ResourceProcessor() {

			@Override
			public void load(boolean override, String nameprefix, AdvancedFile file) {
				//System.out.println(file.isDirectory() == file.isFile());
			}

			@Override
			public void clear() {

			}

		};
		p.addCallback(new LoadingProgressCallback() {

			@Override
			public void onProgressChange(int processed, int all) {
				System.out.println(processed);
				System.out.println("/" + all);
			}
		});
		p.stage(new AdvancedFile("src/main/java"));
		p.processStaged(0.1f);
	}

}
