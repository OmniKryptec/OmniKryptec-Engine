/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.util.debug;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import de.omnikryptec.old.util.logger.Logger;

/**
 * This object does something if a specified value for a variable is detected
 * 
 * @author Panzer1119
 */
public abstract class SignalAwaiter<T> {

    private final Timer timer = new Timer(100, (e) -> checkSignalAndTime());
    private T signal = null;
    private long amount = 0L;
    private TimeUnit unit = null;
    private Instant instant_started = null;

    /**
     * Constructs a SignalAwaiter
     * 
     * @param signal Signal on which is waited
     * @param amount Long Amount of maximum time to be waited for
     * @param unit   TimeUnit Unit for the amount
     */
    public SignalAwaiter(T signal, long amount, TimeUnit unit) {
	this.signal = signal;
    }

    /**
     * Returns the signal
     * 
     * @return Signal
     */
    public final T getSignal() {
	return signal;
    }

    /**
     * Sets the signal
     * 
     * @param signal Signal
     * @return SignalAwaiter A reference to this SignalAwaiter
     */
    public final SignalAwaiter<T> setSignal(T signal) {
	this.signal = signal;
	return this;
    }

    /**
     * Returns the amount of the maximum waiting time
     * 
     * @return Long Amount
     */
    public long getAmount() {
	return amount;
    }

    /**
     * Sets the maximum waiting time
     * 
     * @param amount Long Amount
     * @return SignalAwaiter A reference to this SignalAwaiter
     */
    public SignalAwaiter<T> setAmount(long amount) {
	this.amount = amount;
	return this;
    }

    /**
     * Returns the time unit
     * 
     * @return TimeUnit Time unit
     */
    public TimeUnit getUnit() {
	return unit;
    }

    /**
     * Sets the time unit
     * 
     * @param unit TimeUnit Time unit
     * @return SignalAwaiter A reference to this SignalAwaiter
     */
    public SignalAwaiter<T> setUnit(TimeUnit unit) {
	this.unit = unit;
	return this;
    }

    /**
     * Returns the Instant when this was started
     * 
     * @return Instant Started instant
     */
    public Instant getInstantStarted() {
	return instant_started;
    }

    /**
     * Starts this SignalAwaiter
     */
    public final void start() {
	instant_started = Instant.now();
	timer.start();
	while (timer.isRunning()) {
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
	if (signal == signal_temp || (duration.compareTo(Duration.ofSeconds(unit.toSeconds(amount))) > 0)) {
	    Logger.log(String.format("Stopped timer %s == %s", signal, signal_temp));
	    timer.stop();
	}
    }

    /**
     * Override this so the SignalAwaiter can retrieve the state of the monitored
     * variable
     * 
     * @return Signal
     */
    public abstract T checkSignal();

}
