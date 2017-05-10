package omnikryptec.debug;

import javax.swing.Timer;

/**
 *
 * @author Paul
 */
public abstract class VariableChangeListener {

	private final Timer timer;
	private Object variable = null;
	private boolean firstCheck = true;
	private boolean isChecking = false;

	/**
	 * This Object can be used to detect a change within a variable
	 * 
	 * @param waittime
	 *            Integer Delay in ms between every update
	 */
	public VariableChangeListener(int waittime) {
		timer = new Timer(waittime, e -> updateVariable());
	}

	/**
	 * This function is used to retrieve the variable which gets monitored
	 * 
	 * @return Object New value
	 */
	public abstract Object getVariable();

	/**
	 * This function is called if the variable changed its value
	 * 
	 * @param oldValue
	 *            Object Old value
	 * @param newValue
	 *            Object New Value
	 */
	public abstract void variableChanged(Object oldValue, Object newValue);

	/**
	 * Update function
	 */
	private synchronized void updateVariable() {
		if (isChecking) {
			return;
		}
		isChecking = true;
		new Thread(() -> {
			try {
				Object o = getVariable();
				if (o != variable && !firstCheck) {
					variableChanged(variable, o);
				} else if (firstCheck) {
					firstCheck = false;
				}
				variable = o;
			} catch (Exception ex) {
				System.err.println("Error while updating the value: " + ex);
			}
			isChecking = false;
		}).start();
	}

	/**
	 * Starts the VariableChangeListener
	 * 
	 * @return Boolean True if it worked, False if not
	 */
	public final boolean start() {
		if (timer.isRunning()) {
			return false;
		} else {
			firstCheck = true;
			timer.start();
			return true;
		}
	}

	/**
	 * Stops the VariableChangeListener
	 * 
	 * @return Boolean True if it worked, False if not
	 */
	public final boolean stop() {
		if (!timer.isRunning()) {
			return false;
		} else {
			timer.stop();
			return true;
		}
	}

}
