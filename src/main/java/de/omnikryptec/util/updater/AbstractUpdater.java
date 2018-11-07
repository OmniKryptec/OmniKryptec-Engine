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

package de.omnikryptec.util.updater;

import de.omnikryptec.libapi.LibAPIManager;
import de.omnikryptec.util.data.Smoother;

import javax.annotation.Nonnull;

/**
 * A wrapper class that is responsible to run an {@link #operation()} in a
 * certain time interval and provide various often required functions like
 * {@link #getDeltaTime()} or {@link #getOPS()}.
 *
 * @author pcfreak9000
 */
public class AbstractUpdater {

    private double opstime = 0;
    private double deltatime = 0;
    private double lasttime = 0;
    private double frontruntime = 0;

    private long operationcount = 0;

    private long ops1 = 0, ops2 = 0;
    private boolean ops = true;

    private double lastsynced;

    private Smoother deltaTimeSmoother;

    public AbstractUpdater() {
        this.deltaTimeSmoother = new Smoother();
    }

    /**
     * Updates the window maintained by this object and the values accessable by the
     * functions of this class (e.g. {@link #getDeltaTime()}.<br>
     * The update includes swapping the buffers and polling events.<br>
     * <br>
     * This function can limit the rate of operations by setting this Thread to
     * sleep. This will happen if the operations per second (not counted) are
     * greater than the specified maxops or in other words, if idle time is
     * available.<br>
     * <br>
     *
     * @param maxops limits the OPS for values greater than 0. Otherwise does
     *               nothing.
     */
    public void update(int maxops) {
        double currentFrameTime = LibAPIManager.active().getTime();
        deltatime = (currentFrameTime - lasttime);
        deltaTimeSmoother.push(deltatime);
        frontruntime += deltatime;
        lasttime = currentFrameTime;
        if (maxops > 0) {
            sync(maxops);
        }
        operation();
        operationcount++;
        if (ops) {
            ops1++;
        } else {
            ops2++;
        }
        if (frontruntime - opstime >= 1.0) {
            opstime = frontruntime;
            if (ops) {
                ops = false;
                ops2 = 0;
            } else {
                ops = true;
                ops1 = 0;
            }
        }
    }

    /**
     * Called in the {@link #update(int)} method.<br>
     * This function does not have to be overriden. This class can be used to
     * monitor the stats and sleep, if enabled.
     */
    protected void operation() {
    }

    /**
     * See {@link #update(int)}.
     *
     * @param ops maxops, values smaller or equal to 0 confuse this function though.
     */
    private void sync(int ops) {
        double target = lastsynced + (1.0 / ops);
        try {
            while ((lastsynced = LibAPIManager.active().getTime()) < target) {
                Thread.sleep(1);
            }
        } catch (InterruptedException ex) {
        }
    }

    /**
     * An instance of {@link Smoother} that can be used to retrieve a delta time
     * smoothed over multiple operations, in seconds.
     *
     * @return the delta time smoother
     *
     * @see #getDeltaTime()
     */
    @Nonnull
    public final Smoother getDeltaTimeSmoother() {
        return deltaTimeSmoother;
    }

    /**
     * the amount of calls to the {@link #update(int)} function since the creation
     * of this object.
     *
     * @return the operation count
     */
    public long getOperationCount() {
        return operationcount;
    }

    // /**
    // * the amount of time the maintained window was in the foreground. For a
    // * complete time since glfw initialization, see {@link GLFWManager#getTime()}.
    // *
    // * @return window foreground time
    // * @see GLFWManager#getTime()
    // */
    // public double getFrontRunningTime() {
    // return frontruntime;
    // }

    /**
     * the counted operations per second. Counted means that the calls to
     * {@link #update(int)} will be counted each second, so the value of this
     * function will only change once every second.
     *
     * @return operations per second
     */
    public long getOPS() {
        return ops ? ops2 : ops1;
    }

    /**
     * the measured delta time. That is the elapsed time between the last and the
     * last but one call to {@link #update(int)}, in seconds. For a smoothed value
     * over multiple updates, see {@link #getDeltaTimeSmoother()}.
     *
     * @return delta time
     *
     * @see #getDeltaTimeSmoother()
     */
    public double getDeltaTime() {
        return deltatime;
    }

    /**
     * Sets the deltatime to zero.
     */
    public void resetDeltaTime() {
        this.deltatime = 0;
        this.lasttime = LibAPIManager.active().getTime();
        this.opstime = LibAPIManager.active().getTime();
    }

    public Time asTime() {
        return new Time(getOperationCount(), LibAPIManager.active().getTime(), getDeltaTime());
    }

}
