package omnikryptec.logger;

/**
 *
 * @author Panzer1119
 */
public class LogEntryFormatter {
    
    public static String toggleFormat(String pattern, boolean dt, boolean cl, boolean th, boolean ll, boolean me, boolean ex) {
        if(!dt) {
            pattern = pattern.replaceAll("dt", "");
        }
        if(!cl) {
            pattern = pattern.replaceAll("cl", "");
        }
        if(!th) {
            pattern = pattern.replaceAll("th", "");
        }
        if(!ll) {
            pattern = pattern.replaceAll("ll", "");
        }
        if(!me) {
            pattern = pattern.replaceAll("me", "");
        }
        if(!ex) {
            pattern = pattern.replaceAll("ex", "");
        }
        return pattern;
    }
    
}
