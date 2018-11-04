package de.omnikryptec.core;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import de.omnikryptec.util.Util;
import de.omnikryptec.util.updater.Time;

public class UpdateContainer implements Updateable {

    public static enum UpdateType {
	PRE, MAIN, POST;
    }

    private Multimap<UpdateType, Updateable> updateables;

    public UpdateContainer() {
	updateables = MultimapBuilder.enumKeys(UpdateType.class).arrayListValues().build();
    }

    public void addUpdateable(Updateable updateable, UpdateType... types) {
	Util.ensureNonNull(updateable);
	if (updateable == this) {
	    throw new IllegalArgumentException("Can't add this");
	}
	if (types == null || types.length == 0) {
	    types = UpdateType.values();
	}
	for (UpdateType t : types) {
	    updateables.put(t, updateable);
	}
    }

    @Override
    public void preUpdate(Time time) {
	for (Updateable updt : updateables.get(UpdateType.PRE)) {
	    updt.preUpdate(time);
	}
    }

    @Override
    public void update(Time time) {
	for (Updateable updt : updateables.get(UpdateType.MAIN)) {
	    updt.update(time);
	}
    }

    @Override
    public void postUpdate(Time time) {
	for (Updateable updt : updateables.get(UpdateType.POST)) {
	    updt.postUpdate(time);
	}
    }

}