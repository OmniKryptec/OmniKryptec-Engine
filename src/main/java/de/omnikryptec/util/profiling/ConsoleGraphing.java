package de.omnikryptec.util.profiling;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConsoleGraphing {
    
    public static String graph(int width, int height, List<? extends Number> data, String title, String xName, String yName) {
        String string = title+"\n";
        double xDiv = data.size() == width ? 1 : data.size() / (double) width;
        int[] values = new int[width];
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int i = 0; i < values.length; i++) {
            List<? extends Number> sub = data.subList((int) Math.floor(xDiv * i),
                    Math.min((int) Math.ceil(xDiv * (i + 1)), data.size()));
            double sum = 0;
            for (Number l : sub) {
                sum += l.doubleValue();
            }
            values[i] = (int) (Math.round(sum / (double) sub.size()));
            min = Math.min(min, values[i]);
            max = Math.max(max, values[i]);
        }
        double yDiv = (max - min) / (double) height;
        int offset = Math.max((max + "").length(), (min + "").length());
        for (int y = yName != null ? -1 : 0; y < height; y++) {
            if (y == -1) {
                string += rep((int) Math.round((offset + 3) - (yName.length() / 2.0)), " ") + yName + "\n";
                continue;
            }
            int yValue = (int) (Math.round((height - y - 1) * yDiv) + min);
            if (yValue == 0) {
                continue;//just skip zero :)
            }
            for (int x = -2; x < width; x++) {
                if (x == -2) {
                    int l = (yValue + "").length();
                    string += rep(offset - l, " ") + yValue + (yValue < 0 ? "=>" : "=<");
                } else if (x == -1) {
                    string += "|";
                } else {
                    if (yValue < 0 && values[x] < 0 && values[x] <= yValue) {
                        string += "#";
                    } else if (yValue > 0 && values[x] > 0 && values[x] >= yValue) {
                        string += "#";
                    } else {
                        string += " ";
                    }
                }
            }
            string += "\n";
        }
        string += "x-Scale: Width of # ^= " + String.format("%.2f", xDiv) + (xName == null ? "" : xName) + "\n";
        return string;
    }
    
    private static String rep(int i, String c) {
        String s = "";
        for (int k = 0; k < i; k++) {
            s += c;
        }
        return s;
    }
}
