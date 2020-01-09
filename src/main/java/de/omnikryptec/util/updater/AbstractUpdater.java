/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import de.omnikryptec.libapi.exposed.LibAPIManager;
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

    private final double starttime;

    private double opstime = 0;
    private double deltatime = 0;
    private double lasttime = 0;
    private double frontruntime = 0;

    private long operationcount = 0;

    private long ops1 = 0, ops2 = 0;
    private boolean ops = true;

    private double lastsynced;

    private int maxops = 144;

    private final Smoother deltaTimeSmoother;

    public AbstractUpdater() {
        this.deltaTimeSmoother = new Smoother();
        this.starttime = LibAPIManager.instance().getGLFW().getTime();
    }

    /**
     * Updates this object and the values accessable by the functions of this class
     * (e.g. {@link #getDeltaTime()}.<br>
     * The update includes swapping the buffers and polling events.<br>
     * <br>
     * This function can limit the rate of operations by setting this Thread to
     * sleep. This will happen if the operations per second (not counted) are
     * greater than the specified maxops or in other words, if idle time is
     * available.<br>
     * <br>
     *
     * @see #setMaxOps(int)
     */
    public void update() {
        final double currentFrameTime = LibAPIManager.instance().getGLFW().getTime();
        this.deltatime = (currentFrameTime - this.lasttime);
        this.deltaTimeSmoother.push(this.deltatime);
        this.frontruntime = currentFrameTime - this.starttime;
        this.lasttime = currentFrameTime;
        if (this.maxops > 0) {
            sync(this.maxops);
        }
        this.operationcount++;
        if (this.ops) {
            this.ops1++;
        } else {
            this.ops2++;
        }
        if (this.frontruntime - this.opstime >= 1.0) {
            this.opstime = this.frontruntime;
            if (this.ops) {
                this.ops = false;
                this.ops2 = 0;
            } else {
                this.ops = true;
                this.ops1 = 0;
            }
        }
    }

    /**
     * See {@link #update(int)}.
     *
     * @param ops maxops, values smaller or equal to 0 confuse this function though.
     */
    private void sync(final int ops) {
        final double target = this.lastsynced + (1.0 / ops);
        try {
            while ((this.lastsynced = LibAPIManager.instance().getGLFW().getTime()) < target) {
                Thread.sleep(1);
            }
        } catch (final InterruptedException ex) {
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
        return this.deltaTimeSmoother;
    }

    /**
     * the amount of calls to the {@link #update(int)} function since the creation
     * of this object.
     *
     * @return the operation count
     */
    public long getOperationCount() {
        return this.operationcount;
    }

    /**
     * the counted operations per second. Counted means that the calls to
     * {@link #update(int)} will be counted each second, so the value of this
     * function will only change once every second.
     *
     * @return operations per second
     */
    public long getOPS() {
        return this.ops ? this.ops2 : this.ops1;
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
        return this.deltatime;
    }

    /**
     * Sets the deltatime to zero.
     */
    public void resetDeltaTime() {
        this.deltatime = 0;
        this.lasttime = LibAPIManager.instance().getGLFW().getTime();
        this.opstime = LibAPIManager.instance().getGLFW().getTime();
    }

    public int getMaxOps() {
        return this.maxops;
    }

    /**
     * Sets the max operations per second.
     *
     * @param maxops value
     */
    public void setMaxOps(int maxops) {
        this.maxops = maxops;
    }

    public Time asTime() {
        return new Time(getOperationCount(), getOPS(), LibAPIManager.instance().getGLFW().getTime(), getDeltaTime());
    }

}
