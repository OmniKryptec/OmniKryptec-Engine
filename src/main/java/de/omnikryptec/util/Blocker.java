package de.omnikryptec.util;

/**
 *
 * @author Panzer1119
 */
public class Blocker {

	private int refreshTime = 1;
	private boolean isBlocked = false;

	public Blocker() {
		this(1);
	}

	public Blocker(int refreshTime) {
		this.refreshTime = refreshTime;
	}

	public final void waitFor() {
		while (isBlocked) {
			try {
				Thread.sleep(refreshTime);
			} catch (Exception ex) {
			}
		}
	}

	public final boolean isBlocked() {
		return isBlocked;
	}

	public final Blocker setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
		return this;
	}

	public final int getRefreshTime() {
		return refreshTime;
	}

	public final Blocker setRefreshTime(int refreshTime) {
		this.refreshTime = refreshTime;
		return this;
	}

}
