package de.omnikryptec.util.debug;

import javax.swing.*;

/**
 * Listener which does something if a variable gets changed
 * 
 * @author Panzer1119
 */
public abstract class VariableChangeListener<T> {

	private final Timer timer;
	private T variable = null;
	private boolean firstCheck = true;
	private boolean isChecking = false;

	/**
	 * This Object can be used to detect a change within a variable
	 * 
	 * @param waittime
	 *            Integer Delay in ms between every update
	 */
	public VariableChangeListener(int waittime) {
		timer = new Timer(waittime, (e) -> updateVariable());
	}

	/**
	 * This function is used to retrieve the variable which gets monitored
	 * 
	 * @return Object New value
	 */
	public abstract T getVariable();

	/**
	 * This function is called if the variable changed its value
	 * 
	 * @param oldValue
	 *            Object Old value
	 * @param newValue
	 *            Object New value
	 */
	public abstract void variableChanged(T oldValue, T newValue);

	private synchronized void updateVariable() {
		if (isChecking) {
			return;
		}
		isChecking = true;
		new Thread(() -> {
			try {
				T o = getVariable();
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
	 * @return <tt>true</tt> if the VariableChangeListener was started
	 *         successfully
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
	 * @return <tt>true</tt> if the VariableChangeListener was stopped
	 *         successfully
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
