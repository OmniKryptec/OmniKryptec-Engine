package de.omnikryptec.libapi.exposed;

import java.util.ArrayList;
import java.util.List;

public class AutoDeletionManager {
    private static final List<Deletable> ALL = new ArrayList<>();

    static {
        LibAPIManager.registerResourceShutdownHooks(() -> cleanup());
    }

    private static void cleanup() {
        while (!ALL.isEmpty()) {
            ALL.get(0).deleteAndUnregister();
        }
    }

    public static void unregister(final Deletable d) {
        ALL.remove(d);
    }

    public static void register(final Deletable d) {
        ALL.add(d);
    }
}
