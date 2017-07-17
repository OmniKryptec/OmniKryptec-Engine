package omnikryptec.util.action;

import java.util.Arrays;
import java.util.LinkedList;
import omnikryptec.util.logger.Logger;

/**
 * ActionProcessor
 *
 * @author Panzer1119
 */
public class ActionProcessor extends Thread {
    
    public static final ActionProcessor PROCESSOR = new ActionProcessor();
    
    private final LinkedList<Action> actions = new LinkedList<>();
    private boolean running = false;
    
    public ActionProcessor(Action... actions) {
        addActions(actions);
        start();
    }
    
    public final synchronized  ActionProcessor addActions(Action... actions) {
        final boolean isPaused = !hasActions();
        this.actions.addAll(Arrays.asList(actions));
        if (isPaused) {
            notify();
        }
        return this;
    }
    
    @Override
    public final synchronized void run() {
        while (running || hasActions()) {
            if (hasActions()) {
                acceptNextAction().doAction();
            } else {
                try {
                    wait();
                } catch (Exception ex) {
                    if (Logger.isDebugMode()) {
                        Logger.logErr("Error while waiting for new Actions: " + ex, ex);
                    }
                }
            }
        }
    }
    
    public final synchronized ActionProcessor killNow() {
        actions.clear();
        return kill();
    }
    
    public final synchronized ActionProcessor kill() {
        running = false;
        notify();
        return this;
    }
    
    public final synchronized  Action acceptNextAction() {
        return actions.pollFirst();
    }
    
    public final synchronized  boolean hasActions() {
        return !actions.isEmpty();
    }
    
    public static final void addActionsToProcessor(Action... actions) {
        PROCESSOR.addActions(actions);
    }

}
