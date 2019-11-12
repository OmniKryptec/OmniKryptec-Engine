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
    
    private static final int PAD_TOP = 0;
    private static final int PAD_LEFT = 1;
    private static final int PAD_BOTTOM = 2;
    private static final int PAD_RIGHT = 3;
    
    private static final int DESIRED_PADDING = 3;
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
    private Map<Integer, FontCharacter> metaData;
    
    private FontParser() {
        this.values = new HashMap<>();
    }
    
    public FontFile parse(InputStream stream) {
        this.metaData = new HashMap<>();
        this.spaceWidth = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        //Padding data
        readNextLine();
        this.padding = getValuesOfVariable("padding");
        this.paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT];
        this.paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM];
        //Line sizes
        readNextLine();
        //int lineHeightPixels = getValueOfVariable("lineHeight") - paddingHeight;
        //verticalPerPixelSize = TextMeshCreator.LINE_HEIGHT / (double) lineHeightPixels;
        //horizontalPerPixelSize = verticalPerPixelSize / aspectRatio;
        int imageWidth = getValueOfVariable("scaleW");
        loadCharacterData(imageWidth);
        try {
            reader.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return new FontFile(metaData, spaceWidth);
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
        for (String part : line.split(SPLITTER)) {
            String[] valuePairs = part.split("=");
            if (valuePairs.length == 2) {
                values.put(valuePairs[0], valuePairs[1]);
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
                metaData.put(c.getId(), c);
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
            this.spaceWidth = (getValueOfVariable("xadvance") - paddingWidth);//* horizontalPerPixelSize;
            return null;
        }
        double xTex = ((double) getValueOfVariable("x") + (padding[PAD_LEFT] - DESIRED_PADDING)) / imageSize;
        double yTex = ((double) getValueOfVariable("y") + (padding[PAD_TOP] - DESIRED_PADDING)) / imageSize;
        int width = getValueOfVariable("width") - (paddingWidth - (2 * DESIRED_PADDING));
        int height = getValueOfVariable("height") - ((paddingHeight) - (2 * DESIRED_PADDING));
        double quadWidth = width;// * horizontalPerPixelSize;
        double quadHeight = height;//* verticalPerPixelSize;
        double xTexSize = (double) width / imageSize;
        double yTexSize = (double) height / imageSize;
        double xOff = (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING);// * horizontalPerPixelSize;
        double yOff = (getValueOfVariable("yoffset") + (padding[PAD_TOP] - DESIRED_PADDING));// * verticalPerPixelSize;
        double xAdvance = (getValueOfVariable("xadvance") - paddingWidth);//* horizontalPerPixelSize;
        return new FontCharacter(id, xTex, yTex, xTexSize, yTexSize, xOff, yOff, quadWidth, quadHeight, xAdvance);
    }
}
