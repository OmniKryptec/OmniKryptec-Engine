package de.omnikryptec.core;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

public class UpdateContainer {

    public static enum UpdateTime {
	PRE, MIDDLE, POST;
    }

    private Multimap<UpdateTime, Updateable> updateables;

    public UpdateContainer() {
	updateables = MultimapBuilder.enumKeys(UpdateTime.class).arrayListValues().build();
    }

    public void update() {
	for(UpdateTime time : UpdateTime.values()) {
	    for(Updateable updt : updateables.get(time)) {
		updt.update(null);
	    }
	}
    }

}
