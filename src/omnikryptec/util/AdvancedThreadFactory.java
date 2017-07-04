package omnikryptec.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AdvancedThreadFactory
 *
 * @author Panzer1119
 */
public class AdvancedThreadFactory implements ThreadFactory {

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private String name;

    public AdvancedThreadFactory() {
        this("");
    }
    
    public AdvancedThreadFactory(String name) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.name = name;
    }

    @Override
    public final Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, String.format(name, threadNumber.getAndIncrement()), 0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

    public final String getName() {
        return name;
    }

    public final AdvancedThreadFactory setName(String name) {
        this.name = name;
        return this;
    }
    
}
