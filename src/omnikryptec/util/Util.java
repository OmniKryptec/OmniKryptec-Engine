package omnikryptec.util;

import static omnikryptec.util.AdvancedFile.NOT_FOUND;

import omnikryptec.display.DisplayManager;
import omnikryptec.util.logger.Logger;

/**
 * Util
 * @author Panzer1119
 */
public class Util {

    public static final String replaceAll(String string, String toReplace, String with) {
        if (string == null || toReplace == null || with == null) {
            return "";
        }
        while (string.indexOf(toReplace) != NOT_FOUND) {
            string = string.replace("\\" + toReplace, with);
        }
        return string;
    }

    public static final String[] adjustLength(String[] array, boolean returnnew) {
        String[] names = array;
        int tmp = 0;
        for (int i = 0; i < names.length; i++) {
            tmp = Math.max(tmp, names[i].length());
        }
        if (returnnew) {
            array = new String[names.length];
        }
        StringBuilder s;
        for (int i = 0; i < names.length; i++) {
            s = new StringBuilder(names[i]);
            while (s.length() < tmp) {
                s.append(' ');
            }
            array[i] = s.toString();
        }
        return array;
    }

    public static final String[] merge(Object... b) {
        String[][] array = createString2d(b);
        String[] newone = new String[]{""};
        for (int i = 0; i < array.length; i++) {
            newone = mergeS(newone, array[i]);
        }
        return newone;
    }

    public static final String[] mergeS(String[] a, String[] b) {
        if (a.length == 0) {
            return b;
        }
        if (b.length == 0) {
            return a;
        }
        String[] newone = new String[Math.max(a.length, b.length)];
        for (int i = 0; i < newone.length; i++) {
            newone[i] = (a.length == 1 ? a[0] : a[i]) + (b.length == 1 ? b[0] : b[i]);
        }
        return newone;
    }

    private static String[][] createString2d(Object[] objs) {
        String[][] array = new String[objs.length][];
        for (int i = 0; i < array.length; i++) {
            if (objs[i] instanceof String[]) {
                array[i] = (String[]) objs[i];
            } else if (objs[i] instanceof String) {
                array[i] = new String[]{(String) objs[i]};
            }
        }
        return array;
    }
    
    /**
     * Stops a Thread until its dead or the Time is over
     * @param thread Thread to get stopped
     * @param delayTime Time between each stopping try in milliseconds
     * @param maxTime Maximum time to wait for in milliseconds
     */
    public static final void killThread(Thread thread, int delayTime, int maxTime) {
        if(thread == null) {
            return;
        }
        int times = 0;
        while((thread.isAlive() && !thread.isInterrupted()) && ((delayTime == -1 || maxTime == -1) || ((times * delayTime) < maxTime))) {
            try {
                thread.interrupt();
                //FIXME deprecated?!
                thread.stop();
                if(delayTime > 0) {
                    Thread.sleep(delayTime);
                }
            } catch (Exception ex) {
                if(Logger.isDebugMode()) {
                    Logger.logErr(String.format("Error while killing Thread \"%s\": %s", thread.getName(), ex), ex);
                }
            }
            times++;
        }
    }
    
    public static final double getCurrentTime() {
        try {
            return DisplayManager.instance().getCurrentTime();
        } catch (Exception ex) {
            return System.currentTimeMillis();
        }
    }

}
