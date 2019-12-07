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
    
    private Map<String, String> values;
    
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
        reader = new BufferedReader(new InputStreamReader(stream));
        //Padding data
        readNextLine();
        this.padding = getValuesOfVariable("padding");
        this.paddingWidth = padding[INDEX_PAD_LEFT] + padding[INDEX_PAD_RIGHT];
        this.paddingHeight = padding[INDEX_PAD_TOP] + padding[INDEX_PAD_BOTTOM];
        String name = values.get("face");
        //Line sizes
        readNextLine();
        int lineHeightPixels = getValueOfVariable("lineHeight") - paddingHeight;
        verticalPerPixelSize = 1 / (double) lineHeightPixels;
        horizontalPerPixelSize = verticalPerPixelSize / 1;
        int baseInt = getValueOfVariable("base");
        int imageWidth = getValueOfVariable("scaleW");
        base = baseInt / (double) lineHeightPixels;
        loadCharacterData(imageWidth);
        try {
            reader.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return new FontFile(metaData, (float) spaceWidth, name, (float) base, (float)verticalPerPixelSize);
    }
    
    private boolean readNextLine() {
        values.clear();
        String line = null;
        try {
            line = reader.readLine();
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
                values.put(valuePairs[0], valuePairs[1].replace("\"", ""));
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
        return Integer.parseInt(values.get(variable));
    }
    
    /**
     * Gets the array of ints associated with a variable on the current line.
     * 
     * @param variable - the name of the variable.
     * @return The int array of values associated with the variable.
     */
    private int[] getValuesOfVariable(String variable) {
        String[] numbers = values.get(variable).split(NUMBER_SEPARATOR);
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
                metaData.put((char) c.getAscii(), c);
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
            this.spaceWidth = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
            return null;
        }
        double xTex = ((double) getValueOfVariable("x") + (padding[INDEX_PAD_LEFT] - DESIRED_PADDING)) / imageSize;
        double yTex = ((double) getValueOfVariable("y") + (padding[INDEX_PAD_TOP] - DESIRED_PADDING)) / imageSize;
        int width = getValueOfVariable("width") - (paddingWidth - (2 * DESIRED_PADDING));
        int height = getValueOfVariable("height") - ((paddingHeight) - (2 * DESIRED_PADDING));
        double quadWidth = width * horizontalPerPixelSize;
        double quadHeight = height * verticalPerPixelSize;
        double xTexSize = (double) width / imageSize;
        double yTexSize = (double) height / imageSize;
        double xOff = (getValueOfVariable("xoffset") + (padding[INDEX_PAD_LEFT] - DESIRED_PADDING)) * horizontalPerPixelSize;
        double yOff = (getValueOfVariable("yoffset") + (padding[INDEX_PAD_TOP] - DESIRED_PADDING)) * verticalPerPixelSize;
        double xAdvance = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
        return new FontCharacter(id, (float) xTex, (float) yTex, (float) xTexSize, (float) yTexSize, (float) xOff,
                (float) yOff, (float) quadWidth, (float) quadHeight, (float) xAdvance);
    }
}
