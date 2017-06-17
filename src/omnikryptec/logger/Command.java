package omnikryptec.logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import omnikryptec.event.Event;
import omnikryptec.event.EventSystem;
import omnikryptec.event.EventType;
import omnikryptec.exceptions.UnsupportedCharacterException;

/**
 *
 * @author Panzer1119
 */
public class Command {

	public static final String COMMANDSTART = "/";
	public static final String ESCAPESTRING = "\\";
	public static final String ESCAPESPACESTRING = "\"";
	public static final HashMap<String, Command> COMMANDS = new HashMap<>();

	private String command = "";
	private String help = "";
	private boolean usesArguments = false;
	private boolean runAlways = false;
	private boolean hasExtraThread = false;

	private static final ExecutorService commandExecutor = Executors.newFixedThreadPool(10);

	public Command(String command) {
		try {
			setCommand(command);
		} catch (UnsupportedCharacterException ucex) {
			Logger.logErr("Error while creating command: " + ucex, ucex);
		}
	}

	public void run(String arguments) {
		Logger.log(this);
	}

	private final void preRun(String arguments) {
		if (!arguments.isEmpty() && !usesArguments) {
			Logger.log("Command uses no arguments", LogEntry.LogLevel.ERROR);
			return;
		}
		run(arguments);
	}

	public final Command delete() {
		COMMANDS.remove(command);
		return this;
	}

	public final Command setHasExtraThread(boolean hasExtraThread) {
		this.hasExtraThread = hasExtraThread;
		return this;
	}

	public final boolean hasExtraThread() {
		return hasExtraThread;
	}

	public static final String[] getArguments(String arguments) {
		return Command.getArguments(arguments, " ");
	}

	private static final String[] getArguments(String arguments, String delimiter) {
		boolean isArg = false;
		String temp = "";
		final ArrayList<String> args = new ArrayList<>();
		for (int i = 0; i < arguments.length(); i++) {
			char c = arguments.charAt(i);
			String c_string = "" + c;
			switch (c_string) {
			case ESCAPESTRING:
				i++;
				if (arguments.length() > i) {
					char c_2 = arguments.charAt(i);
					temp += c_2;
				}
				break;
			case ESCAPESPACESTRING:
				isArg = !isArg;
				break;
			default:
				if ((!c_string.equals(delimiter) || isArg)) {
					temp += c;
				} else {
					if (!temp.isEmpty()) {
						args.add(temp);
					}
					temp = "";
				}
				break;
			}
		}
		if (!temp.isEmpty()) {
			args.add(temp);
		}
		String[] args_s = args.toArray(new String[args.size()]);
		return args_s;
	}

	public static final boolean runCommand(String command) {
		for (String c : COMMANDS.keySet()) {
			try {
				if (((command.contains(" ") ? command.substring(0, command.indexOf(" ")) : command)).equals(c)) {
					String arguments_temp = "";
					if (command.contains(" ")) {
						arguments_temp = command.substring(command.indexOf(" ") + 1);
					}
					final String arguments = arguments_temp;
					Command cc = COMMANDS.get(c);
					Runnable run = () -> {
						try {
							if (cc.isRunningAlways()) {
								COMMANDS.get(c).run(arguments);
							} else {
								COMMANDS.get(c).preRun(arguments);
							}
						} catch (Exception ex) {
						}
					};
					Runnable run_2 = () -> {
						try {
							EventSystem.instance().fireEvent(new Event(COMMANDS.get(c), arguments), EventType.COMMAND);
						} catch (Exception ex) {
						}
					};
					if (cc.hasExtraThread) {
						commandExecutor.execute(run);
					} else {
						run.run();
					}
					commandExecutor.execute(run_2);
					return true;
				}
			} catch (Exception ex) {
				Logger.logErr("Error while searching for a command: " + ex, ex);
			}
		}
		return false;
	}

	public final String getCommand() {
		return command;
	}

	public final Command setCommand(String command) throws UnsupportedCharacterException {
		if (command.contains(" ")) {
			StackTraceElement e = Thread.currentThread().getStackTrace()[Thread.currentThread().getStackTrace().length
					- 1];
			throw new UnsupportedCharacterException(String.format("%s.%s:%s UnsupportedCharacterException at %d",
					e.getClassName(), e.getMethodName(), e.getLineNumber(), command.indexOf(" ")));
		}
		delete();
		this.command = command;
		COMMANDS.put(command, this);
		return this;
	}

	public final boolean isUsingArguments() {
		return usesArguments;
	}

	public final Command setUseArguments(boolean usesArguments) {
		this.usesArguments = usesArguments;
		return this;
	}

	public final String getHelp() {
		return help;
	}

	public final Command setHelp(String help) {
		this.help = help;
		return this;
	}

	public final boolean isRunningAlways() {
		return runAlways;
	}

	public final Command setRunAlways(boolean runAlways) {
		this.runAlways = runAlways;
		return this;
	}

	@Override
	public final String toString() {
		return command + ": " + help;
	}

	@Deprecated
	public static final String[] getArgumentsOld(String arguments) {
		return getArguments(arguments, " ");
	}

	@Deprecated
	public static final String[] getArgumentsOld(String arguments, String split) {
		return arguments.split(split);
	}

}
