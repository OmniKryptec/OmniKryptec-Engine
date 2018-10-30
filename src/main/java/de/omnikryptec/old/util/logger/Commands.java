/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.util.logger;

import de.omnikryptec.old.main.OmniKryptecEngine;
import de.omnikryptec.old.main.OmniKryptecEngine.ShutdownOption;
import de.omnikryptec.old.util.EnumCollection.GameState;

/**
 * @author Panzer1119
 */
public class Commands {

    public static final Command COMMANDEXIT = new Command("exit") {

	@Override
	public void run(String arguments) {
	    ShutdownOption shutdownOption = ShutdownOption.JAVA;
	    boolean instantShutdown = false;
	    if (!arguments.isEmpty()) {
		boolean found = false;
		String[] args = getArguments(arguments);
		for (String g : args) {
		    if (g.equalsIgnoreCase("-engine")) {
			shutdownOption = ShutdownOption.ENGINE;
			found = true;
		    } else if (g.equalsIgnoreCase("-java")) {
			instantShutdown = true;
			found = true;
		    }
		}
		if (!found) {
		    Logger.log(getHelp());
		    return;
		}
	    }
	    try {
		if (instantShutdown) {
		    shutdownCompletely();
		} else {
		    if (OmniKryptecEngine.instance() != null) {
			final GameState state = OmniKryptecEngine.instance().getState();
			if (state != GameState.RUNNING && state != GameState.STARTING) {
			    Logger.log("Engine is not running", LogLevel.WARNING);
			} else {
			    OmniKryptecEngine.instance().shutdown();
			    Logger.log("Engine was successfully exited");
			}
		    } else if (shutdownOption == ShutdownOption.ENGINE) {
			Logger.log("Engine does not exist", LogLevel.WARNING);
		    }
		    if (shutdownOption == ShutdownOption.JAVA) {
			shutdownCompletely();
		    }
		}
	    } catch (Exception ex) {
		if (shutdownOption == ShutdownOption.JAVA) {
		    shutdownCompletely();
		} else if (ex instanceof NullPointerException) {
		    Logger.log("Engine does not exist", LogLevel.WARNING);
		} else {
		    Logger.logErr("Error while shutting down the engine: " + ex, ex);
		}
	    }
	}

    }.setUseArguments(true).setHasExtraThread(true).setHelp(
	    "Usage:\nexit [-engine/-java]\nParameter - Description\nengine - Stops only the engine\njava - Stops directly the JVM");

    public static final Command COMMANDTEST = new Command("test") {

	@Override
	public void run(String arguments) {
	    try {
		String[] args = getArguments(arguments);
		for (String arg : args) {
		    Logger.log(arg);
		}
	    } catch (Exception ex) {
		Logger.logErr("Error while executing command \"test " + arguments + "\": " + ex, ex);
	    }
	}

    }.setUseArguments(true).setHasExtraThread(true).setHelp("Usage:\ntest <...>");

    public static final void initialize() {
	// Nothing, this function only registers automatically all standard
	// Command's
    }

    private static final void shutdownCompletely() {
	while (true) {
	    try {
		System.exit(0);
	    } catch (Exception ex) {
		System.exit(-1);
	    }
	}
    }

}
