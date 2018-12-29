package de.omnikryptec.libapi.exposed;

import java.util.ArrayList;
import java.util.List;

public abstract class AutoDelete {

    private static final List<AutoDelete> ALL = new ArrayList<>();

    static {
        LibAPIManager.registerResourceShutdownHooks(() -> cleanup());
    }

    private static void cleanup() {
        while (!ALL.isEmpty()) {
            ALL.get(0).delete();
        }
    }

    public AutoDelete() {
        ALL.add(this);
    }

    public final void delete() {
        ALL.remove(this);
        this.deleteRaw();
    }

    protected abstract void deleteRaw();

}
