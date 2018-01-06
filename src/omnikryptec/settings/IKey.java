package omnikryptec.settings;

import de.codemakers.serialization.XMLable;

/**
 * Key Interface
 *
 * @author Panzer1119
 */
public interface IKey extends XMLable {

    /**
     * Returns the name
     *
     * @return String Name
     */
    public String getName();

    /**
     * Returns if this IKey is pressed
     *
     * @return <tt>true</tt> if it is pressed
     */
    public boolean isPressed();

    /**
     * Returns of this IKey is pressed for a specified time
     *
     * @param minTime Float Minimum pressing time
     * @param maxTime Float Maximum pressing time
     * @return <tt>true</tt> if it is pressed for the specified time
     */
    public boolean isLongPressed(double minTime, double maxTime);

}
