package omnikryptec.util;

/**
 *
 * @author Panzer1119
 */
public class BooleanHolder {
    
    private boolean data = false;
    
    public BooleanHolder() {
        this(false);
    }
    
    public BooleanHolder(boolean data) {
        this.data = data;
    }

    public final boolean isData() {
        return data;
    }

    public final BooleanHolder setData(boolean data) {
        this.data = data;
        return this;
    }
    
}
