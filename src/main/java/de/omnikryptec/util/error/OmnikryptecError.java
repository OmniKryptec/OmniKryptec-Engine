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

package de.omnikryptec.util.error;

import de.omnikryptec.util.logger.LogLevel;
import de.omnikryptec.util.logger.Logger;
import de.omnikryptec.util.profiler.Profiler;

public class OmnikryptecError implements ErrorItem {

    private final ErrorItem[] info;
    private final int limiterlimit;

    public OmnikryptecError(ErrorItem... info) {
        this(125, info);
    }

    public OmnikryptecError(Throwable t, ErrorItem... array) {
        this(125, array, new NameItem(), new ThroweableItem(t), new Profiler(), new SystemInfoItem());
    }

    public OmnikryptecError(int limiterlimit, ErrorItem[] array, ErrorItem... info) {
        this.limiterlimit = limiterlimit;
        if (array == null) {
            array = new ErrorItem[0];
        }
        if (info == null) {
            info = new ErrorItem[0];
        }
        ErrorItem[] fa = new ErrorItem[array.length + info.length];
        for (int i = 0; i < info.length; i++) {
            fa[i] = info[i];
        }
        for (int i = info.length; i < fa.length; i++) {
            fa[i] = array[i - info.length];
        }
        this.info = fa;
    }

    public void print() {
        Logger.log(getString(true, false), LogLevel.ERROR);
    }

    public String getString(boolean startwithnewline, boolean endwithnewline) {
        StringBuilder builder = new StringBuilder();
        if (startwithnewline) {
            builder.append("\n");
        }
        limiter(builder, '#');
        builder.append("\n");
        for (int i = 0; i < info.length; i++) {
            builder.append(info[i].getError().trim()).append("\n");
            if (i < info.length - 1) {
                builder.append("\n");
            }
        }
        limiter(builder, '#');
        if (endwithnewline) {
            builder.append("\n");
        }
        return builder.toString();
    }

    private void limiter(StringBuilder builder, char c) {
        for (int i = 0; i < limiterlimit; i++) {
            builder.append(c);
        }
    }

    @Override
    public String getError() {
        return getString(false, false);
    }

}
