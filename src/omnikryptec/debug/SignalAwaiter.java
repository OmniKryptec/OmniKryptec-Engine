package omnikryptec.debug;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import omnikryptec.logger.Logger;

/**
 *
 * @author Panzer1119
 */
public abstract class SignalAwaiter<T> {
    
    private final Timer timer = new Timer(100, e -> checkSignalAndTime());
    private T signal = null;
    private long amount = 0L;
    private TimeUnit unit = null;
    private Instant instant_started = null;
    
    public SignalAwaiter(T signal, long amount, TimeUnit unit) {
        this.signal = signal;
    }
    
    public final T getSignal() {
        return signal;
    }
    
    public final SignalAwaiter<T> setSignal(T signal) {
        this.signal = signal;
        return this;
    }

    public long getAmount() {
        return amount;
    }

    public SignalAwaiter<T> setAmount(long amount) {
        this.amount = amount;
        return this;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public SignalAwaiter<T> setUnit(TimeUnit unit) {
        this.unit = unit;
        return this;
    }

    public Instant getInstantStarted() {
        return instant_started;
    }
    
    public final void start() {
        instant_started = Instant.now();
        timer.start();
        while(timer.isRunning()) {
            try {
                Thread.sleep(100);
            } catch (Exception ex) {
            }
        }
        Logger.log("Test");
    }
    
    private final void checkSignalAndTime() {
        final Instant instant_now = Instant.now();
        final T signal_temp = checkSignal();
        final Duration duration = Duration.between(instant_started, instant_now);
        if(signal == signal_temp || (duration.compareTo(Duration.ofSeconds(unit.toSeconds(amount))) > 0)) {
            Logger.log(String.format("Stopped timer %s == %s", signal, signal_temp));
            timer.stop();
        }
    }
    
    public abstract T checkSignal();
    
}
