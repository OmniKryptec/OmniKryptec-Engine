package de.omnikryptec.resource.loader;

import java.io.File;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.resource.loadervpc.ResourceProcessor;

public class Test {
	
	public static void main(String[] args) {
		ResourceProcessor p = new ResourceProcessor() {

			@Override
			public void load(boolean override, String nameprefix, AdvancedFile file) {
				System.out.println(file);
			}

			@Override
			public void clear() {
				
			}
			
		};
		p.stage(new AdvancedFile(new File("")));
		p.processStaged(true);
	}
	
}
