package omnikryptec.util;

import static omnikryptec.util.AdvancedFile.NOT_FOUND;

/**
 *
 * @author Panzer1119
 */
public class Util {
    
    public static final String replaceAll(String string, String toReplace, String with) {
        if(string == null || toReplace == null || with == null) {
            return "";
        }
        while(string.indexOf(toReplace) != NOT_FOUND) {
            string = string.replace("\\" + toReplace, with);
        }
        return string;
    }
    
}
