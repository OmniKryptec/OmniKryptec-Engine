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

package de.omnikryptec.util.settings;

import java.awt.*;

public class GameSettingsTest {
    
    public static final int WINDOW_SIZE = 1;
    
    public static final void main(String[] args) throws Exception {
        final GameSettings gameSettings_1 = new GameSettings();
        System.out.println("gameSettings_1=" + gameSettings_1);
        gameSettings_1.set(WINDOW_SIZE, new Dimension(200, 300));
        System.out.println("gameSettings_1=" + gameSettings_1);
        final Dimension window_size = gameSettings_1.get(WINDOW_SIZE);
        System.out.println("window_size=" + window_size);
        System.out.println(gameSettings_1.get(WINDOW_SIZE, Dimension.class));
        
    }
}
