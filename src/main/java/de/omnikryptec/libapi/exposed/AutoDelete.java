/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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
