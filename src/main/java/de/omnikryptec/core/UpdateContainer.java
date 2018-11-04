package de.omnikryptec.core;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.updater.Time;

public class UpdateContainer implements Updateable {
    
    public enum UpdateType {
        PRE,
        MAIN,
        POST
    }
    
    private final Multimap<UpdateType, Updateable> updateables;
    
    public UpdateContainer() {
        updateables = MultimapBuilder.enumKeys(UpdateType.class).arrayListValues().build();
    }
    
    public Multimap<UpdateType, Updateable> getUpdateables() {
        return updateables;
    }
    
    public UpdateContainer addUpdateable(Updateable updateable, UpdateType... updateTypes) {
        Util.ensureNonNull(updateable);
        if (updateable == this) {
            throw new IllegalArgumentException("Can't add this");
        }
        if (updateTypes == null || updateTypes.length == 0) {
            updateTypes = UpdateType.values();
        }
        for (UpdateType updateType : updateTypes) {
            updateables.put(updateType, updateable);
        }
        return this;
    }
    
    @Override
    public void preUpdate(Time time) {
        for (Updateable updateable : updateables.get(UpdateType.PRE)) {
            updateable.preUpdate(time);
        }
    }
    
    @Override
    public void update(Time time) {
        for (Updateable updateable : updateables.get(UpdateType.MAIN)) {
            updateable.update(time);
        }
    }
    
    @Override
    public void postUpdate(Time time) {
        for (Updateable updateable : updateables.get(UpdateType.POST)) {
            updateable.postUpdate(time);
        }
    }
    
}