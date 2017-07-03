package omnikryptec.util.error;

import omnikryptec.util.logger.Logger;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.profiler.Profiler;

public class OmnikryptecError implements ErrorItem {

    private final ErrorItem[] info;
    private final int limiterlimit;

    public OmnikryptecError(ErrorItem... info) {
        this(125, info);
    }

    public OmnikryptecError(Throwable t, ErrorItem... array) {
        this(125, array, new NameItem(), new ThroweableItem(t), new Profiler(), new SystemInfoItem());
    }

    public OmnikryptecError(int limiterlimit, ErrorItem[] array, ErrorItem... info) {
        this.limiterlimit = limiterlimit;
        if (array == null) {
            array = new ErrorItem[0];
        }
        if (info == null) {
            info = new ErrorItem[0];
        }
        ErrorItem[] fa = new ErrorItem[array.length + info.length];
        for (int i = 0; i < info.length; i++) {
            fa[i] = info[i];
        }
        for (int i = info.length; i < fa.length; i++) {
            fa[i] = array[i - info.length];
        }
        this.info = fa;
    }

    public void print() {
        Logger.log(getString(true, false), LogLevel.ERROR);
    }

    public String getString(boolean startwithnewline, boolean endwithnewline) {
        StringBuilder builder = new StringBuilder();
        if (startwithnewline) {
            builder.append("\n");
        }
        limiter(builder, '#');
        builder.append("\n");
        for (int i = 0; i < info.length; i++) {
            builder.append(info[i].getError().trim()).append("\n");
            if (i < info.length - 1) {
                builder.append("\n");
            }
        }
        limiter(builder, '#');
        if (endwithnewline) {
            builder.append("\n");
        }
        return builder.toString();
    }

    private void limiter(StringBuilder builder, char c) {
        for (int i = 0; i < limiterlimit; i++) {
            builder.append(c);
        }
    }

    @Override
    public String getError() {
        return getString(false, false);
    }

}
