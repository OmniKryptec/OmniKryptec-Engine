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

package de.omnikryptec.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import de.omnikryptec.util.Logger;

//Most parsing code is downloaded from ThinMatrix
public class FontParser {
    private static final String SPLITTER = " ";
    private static final String NUMBER_SEPARATOR = ",";
    private static final int SPACE_ASCII = 32;

    private static final int INDEX_PAD_TOP = 0;
    private static final int INDEX_PAD_LEFT = 1;
    private static final int INDEX_PAD_BOTTOM = 2;
    private static final int INDEX_PAD_RIGHT = 3;

    //FIXME pcfreak9000 fix/improve stuff with padding
    private static final int DESIRED_PADDING = 5;
    private static final Logger LOGGER = Logger.getLogger(FontParser.class);

    private static FontParser instance;

    public static FontParser instance() {
        if (instance == null) {
            instance = new FontParser();
        }
        return instance;
    }

    private final Map<String, String> values;

    private BufferedReader reader;
    private int[] padding;
    private int paddingWidth;
    private int paddingHeight;

    private double spaceWidth;
    private Map<Character, FontCharacter> metaData;
    private double verticalPerPixelSize;
    private double horizontalPerPixelSize;
    private double base;

    private FontParser() {
        this.values = new HashMap<>();
    }

    public FontFile parse(InputStream stream) {
        this.metaData = new HashMap<>();
        this.spaceWidth = 0;
        this.reader = new BufferedReader(new InputStreamReader(stream));
        //Padding data
        readNextLine();
        this.padding = getValuesOfVariable("padding");
        this.paddingWidth = this.padding[INDEX_PAD_LEFT] + this.padding[INDEX_PAD_RIGHT];
        this.paddingHeight = this.padding[INDEX_PAD_TOP] + this.padding[INDEX_PAD_BOTTOM];
        String name = this.values.get("face");
        //Line sizes
        readNextLine();
        int lineHeightPixels = getValueOfVariable("lineHeight") - this.paddingHeight;
        this.verticalPerPixelSize = 1 / (double) lineHeightPixels;
        this.horizontalPerPixelSize = this.verticalPerPixelSize / 1;
        int baseInt = getValueOfVariable("base");
        int imageWidth = getValueOfVariable("scaleW");
        this.base = baseInt / (double) lineHeightPixels;
        loadCharacterData(imageWidth);
        try {
            this.reader.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return new FontFile(this.metaData, (float) this.spaceWidth, name, (float) this.base,
                (float) this.verticalPerPixelSize);
    }

    private boolean readNextLine() {
        this.values.clear();
        String line = null;
        try {
            line = this.reader.readLine();
        } catch (IOException e1) {
            LOGGER.error(e1);
        }
        if (line == null) {
            return false;
        }
        String[] data = line.split(SPLITTER);
        for (int i = 0; i < data.length; i++) {
            String[] valuePairs = data[i].split("=");
            if (valuePairs.length == 2) {
                if (valuePairs[1].startsWith("\"")) {
                    while (!valuePairs[1].endsWith("\"")) {
                        i++;
                        valuePairs[1] += " " + data[i];
                    }
                }
                this.values.put(valuePairs[0], valuePairs[1].replace("\"", ""));
            }
        }
        return true;
    }

    /**
     * Gets the {@code int} value of the variable with a certain name on the current
     * line.
     *
     * @param variable - the name of the variable.
     * @return The value of the variable.
     */
    private int getValueOfVariable(String variable) {
        return Integer.parseInt(this.values.get(variable));
    }

    /**
     * Gets the array of ints associated with a variable on the current line.
     *
     * @param variable - the name of the variable.
     * @return The int array of values associated with the variable.
     */
    private int[] getValuesOfVariable(String variable) {
        String[] numbers = this.values.get(variable).split(NUMBER_SEPARATOR);
        int[] actualValues = new int[numbers.length];
        for (int i = 0; i < actualValues.length; i++) {
            actualValues[i] = Integer.parseInt(numbers[i]);
        }
        return actualValues;
    }

    /**
     * Loads in data about each character and stores the data in the
     * {@link Character} class.
     *
     * @param imageWidth - the width of the texture atlas in pixels.
     */
    private void loadCharacterData(int imageWidth) {
        readNextLine();
        readNextLine();
        while (readNextLine()) {
            FontCharacter c = loadCharacter(imageWidth);
            if (c != null) {
                this.metaData.put((char) c.getAscii(), c);
            }
        }
    }

    /**
     * Loads all the data about one character in the texture atlas and converts it
     * all from 'pixels' to 'screen-space' before storing. The effects of padding
     * are also removed from the data.
     *
     * @param imageSize - the size of the texture atlas in pixels.
     * @return The data about the character.
     */
    private FontCharacter loadCharacter(int imageSize) {
        int id = getValueOfVariable("id");
        if (id == SPACE_ASCII) {
            this.spaceWidth = (getValueOfVariable("xadvance") - this.paddingWidth) * this.horizontalPerPixelSize;
            return null;
        }
        double xTex = ((double) getValueOfVariable("x") + (this.padding[INDEX_PAD_LEFT] - DESIRED_PADDING)) / imageSize;
        double yTex = ((double) getValueOfVariable("y") + (this.padding[INDEX_PAD_TOP] - DESIRED_PADDING)) / imageSize;
        int width = getValueOfVariable("width") - (this.paddingWidth - (2 * DESIRED_PADDING));
        int height = getValueOfVariable("height") - ((this.paddingHeight) - (2 * DESIRED_PADDING));
        double quadWidth = width * this.horizontalPerPixelSize;
        double quadHeight = height * this.verticalPerPixelSize;
        double xTexSize = (double) width / imageSize;
        double yTexSize = (double) height / imageSize;
        double xOff = (getValueOfVariable("xoffset") + (this.padding[INDEX_PAD_LEFT] - DESIRED_PADDING))
                * this.horizontalPerPixelSize;
        double yOff = (getValueOfVariable("yoffset") + (this.padding[INDEX_PAD_TOP] - DESIRED_PADDING))
                * this.verticalPerPixelSize;
        double xAdvance = (getValueOfVariable("xadvance") - this.paddingWidth) * this.horizontalPerPixelSize;
        return new FontCharacter(id, (float) xTex, (float) yTex, (float) xTexSize, (float) yTexSize, (float) xOff,
                (float) yOff, (float) quadWidth, (float) quadHeight, (float) xAdvance);
    }
}
