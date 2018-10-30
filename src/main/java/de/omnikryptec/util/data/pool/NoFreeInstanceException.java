package de.omnikryptec.util.data.pool;

/**
 * @see Pool#retrieve()
 * @author pcfreak9000
 *
 */
public class NoFreeInstanceException extends Exception {

    /**
     * ID
     */
    private static final long serialVersionUID = -278590286814990547L;

    public NoFreeInstanceException(String msg) {
	super(msg);
    }

    public NoFreeInstanceException() {
	super();
    }

}
