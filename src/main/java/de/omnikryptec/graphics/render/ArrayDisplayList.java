package de.omnikryptec.graphics.render;

import java.util.ArrayList;

public class ArrayDisplayList<T> implements DisplayList {

    private final ArrayList<T> arl = new ArrayList<>();

    @Override
    public void addObject(final Object o) {
        this.arl.add((T) o);
    }

    public ArrayList<T> get() {
        return this.arl;
    }

}
