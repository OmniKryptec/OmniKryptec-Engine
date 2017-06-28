package omnikryptec.util.profiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.util.Util;

public class Profiler {

    public static final long NAME_NOT_FOUND = -1;
    public static final String OVERALL_FRAME_TIME = "OVERALL_FRAME_TIME";
    public static final String SCENE_TIME = "SCENE_TIME";
    public static final String PARTICLE_RENDERER = "PARTICLE_RENDERER";
    public static final String PARTICLE_UPDATER = "PARTICLE_UPDATER";
    public static final String POSTPROCESSOR = "POSTPROCESSOR";
    public static final String DISPLAY_UPDATE_TIME = "DISPLAY_UPDATE_TIME";
    public static final String DISPLAY_IDLE_TIME = "DISPLAY_IDLE_TIME";

    
    private static final ArrayList<Profilable> PROFILABLES = new ArrayList<>();
    private final List<ProfileContainer> container = new ArrayList<>();

    public static double currentTimeByName(String name) {
        for (int i = 0; i < PROFILABLES.size(); i++) {
            Profilable p = PROFILABLES.get(i);
            if(p == null) {
                continue;
            }
            ProfileContainer[] cs = p.getProfiles();
            if(cs == null) {
                continue;
            }
            for (ProfileContainer c : cs) {
                if (c.getName().equals(name)) {
                    return c.getTime();
                }
            }
        }
        return NAME_NOT_FOUND;
    }

    public static void addProfilable(Profilable p, int index) {
        if (PROFILABLES.contains(p)) {
            Logger.log("The Profilable \"" + p + "\" is already registered!", LogLevel.WARNING);
        }
        while(PROFILABLES.size() < index + 1) {
            PROFILABLES.add(null);
        }
        PROFILABLES.add(index, p);
    }

    public static void addProfilable(Profilable p) {
        if (PROFILABLES.contains(p)) {
            Logger.log("The Profilable \"" + p + "\" is already registered!", LogLevel.WARNING);
        }
        PROFILABLES.add(p);
    }

    public static void removeProfilable(Profilable p) {
        PROFILABLES.remove(p);
    }

    public Profiler() {
        for (int i = 0; i < PROFILABLES.size(); i++) {
            Profilable p = PROFILABLES.get(i);
            if(p == null) {
                continue;
            }
            ProfileContainer[] c = p.getProfiles();
            if(c != null) {
                container.addAll(Arrays.asList(c));
            }
        }
    }

    public double profiledTimeByName(String name) {
        for (ProfileContainer c : container) {
            if(c != null && c.getName().equals(name)) {
                return c.getTime();
            }
        }
        return NAME_NOT_FOUND;
    }

    public String createTimesString(int maxchars, boolean newline, boolean endline) {
        if (container.isEmpty()) {
            Logger.log("No Profiler are registered!", LogLevel.INFO);
            return "";
        }
        double maxtime = profiledTimeByName(OVERALL_FRAME_TIME);
        String[] relatives = createRelativeTo(maxtime);
        String[] mergedlines = Util.merge(createNames(), " |", relatives, "|", createPerc(maxtime), "|", createLines(relatives, maxchars, maxtime), "|");
        StringBuilder str = new StringBuilder();
        if (newline) {
            str.append("\n");
        }
        for (int i = 0; i < mergedlines.length; i++) {
            str.append(mergedlines[i]);
            if (i < mergedlines.length - 1 || endline) {
                str.append("\n");
            }
        }
        return str.toString();
    }

    private String[] createNames() {
        String[] newone = new String[container.size()];
        for (int i = 0; i < newone.length; i++) {
            ProfileContainer c = container.get(i);
            if(c == null) {
                continue;
            }
            newone[i] = c.getName();
        }
        return Util.adjustLength(newone, false);
    }

    private String[] createPerc(double maxtime) {
        String[] array = new String[container.size()];
        for (int i = 0; i < container.size(); i++) {
            ProfileContainer c = container.get(i);
            if(c == null) {
                continue;
            }
            array[i] = c.getPercentage(maxtime);
        }
        return Util.adjustLength(array, false);
    }

    private String[] createRelativeTo(double maxtime) {
        String[] array = new String[container.size()];
        for (int i = 0; i < container.size(); i++) {
            ProfileContainer c = container.get(i);
            if(c == null) {
                continue;
            }
            array[i] = c.getReletiveTo(maxtime);
        }
        return Util.adjustLength(array, false);
    }

    private String[] createLines(String[] relatives, int maxchars, double maxtime) {
    	double f = maxchars / maxtime;
        String[] newone = new String[relatives.length];
        for (int i = 0; i < newone.length; i++) {
            newone[i] = appendLine(Double.parseDouble(relatives[i].split("/")[0].replace("ms", "")), f, maxchars);
        }
        return newone;
    }

    private String appendLine(double time, double mult, int max) {
        StringBuilder b = new StringBuilder();
        long amount = Math.round(time * mult);
        for (int i = 0; i < max; i++) {
            if (i < amount) {
                b.append('=');
            } else {
                b.append('-');
            }
        }
        if (amount > max) {
            b.delete(b.length() - 1, b.length());
            b.append("!");
        }
        return b.toString();
    }

}
