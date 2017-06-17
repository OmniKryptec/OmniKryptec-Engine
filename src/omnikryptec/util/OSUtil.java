package omnikryptec.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;

/**
 *
 * @author Panzer1119
 */
public class OSUtil {

	private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
	private static final File USER_HOME = new File(System.getProperty("user.home"));
	private static final String FOLDER_NAME = "." + OmniKryptecEngine.class.getSimpleName();
	private static final String PATHSEPARATOR = "/";

	public static final OS OpSys = detectOS();
	public static final File STANDARDAPPDATA = getStandardAppDataFolder();

	public static enum OS {
		WINDOWS("windows"), MAC("macosx"), UNIX("linux"), SOLARIS("solaris"), ERROR(null);

		private final String name;

		OS(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String toPathForResource(String nativesPath) {
			return (nativesPath.startsWith(PATHSEPARATOR) ? "" : PATHSEPARATOR) + nativesPath
					+ (nativesPath.endsWith(PATHSEPARATOR) ? "" : PATHSEPARATOR) + name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public static final OS getOS() {
		return OpSys;
	}

	private static final OS detectOS() {
		if (OS_NAME.contains("win")) {
			return OS.WINDOWS;
		} else if (OS_NAME.contains("mac")) {
			return OS.MAC;
		} else if (OS_NAME.contains("nix") || OS_NAME.contains("nux") || OS_NAME.contains("aix")) {
			return OS.UNIX;
		} else if (OS_NAME.contains("sunos")) {
			return OS.SOLARIS;
		} else {
			return OS.ERROR;
		}
	}

	public static final boolean createStandardFolders() {
		try {
			STANDARDAPPDATA.mkdirs();
			return (STANDARDAPPDATA.exists() && STANDARDAPPDATA.isDirectory());
		} catch (Exception ex) {
			Logger.logErr("Error while creating standard folders: " + ex, ex);
			return false;
		}
	}

	public static final File getStandardAppDataFolder() {
		return getAppDataFolder(FOLDER_NAME);
	}

	public static final File getAppDataFolder(String folderName) {
		File file = null;
		switch (OpSys) {
		case WINDOWS:
			file = new File(USER_HOME.getAbsolutePath() + File.separator + "AppData" + File.separator + "Roaming"
					+ File.separator + folderName);
			break;
		case MAC:
			file = new File(USER_HOME.getAbsolutePath() + File.separator + "Library" + File.separator
					+ "Application Support" + File.separator + folderName); // TODO
																			// Needs
																			// confirmation!
			break;
		case UNIX:
			file = new File(USER_HOME.getAbsolutePath() + File.separator + folderName);
			break;
		case SOLARIS:
			file = new File(USER_HOME.getAbsolutePath() + File.separator + folderName);
			break;
		case ERROR:
			break;
		default:
			break;
		}
		return file;
	}

	public static final boolean extractFileFromJar(File file, String path) {
		if (file.exists()) {
			return true;
		}
		try {
			final InputStream inputStream = OSUtil.class.getResourceAsStream(path);
			Files.copy(inputStream, file.getAbsoluteFile().toPath());
			inputStream.close();
			return file.exists();
		} catch (Exception ex) {
			Logger.logErr("Error while extracting file from jar: " + ex, ex);
			return false;
		}
	}

	public static final boolean extractFolderFromJar(File folder, String path) {
		try {
			boolean allGood = true;
			final File jarFile = getJarFile();
			if (jarFile.isFile()) {
				final JarFile jar = new JarFile(jarFile);
				final Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					final JarEntry jarEntry = entries.nextElement();
					final String name = jarEntry.getName();
					if (name.startsWith(path)) {
						boolean good = extractFileFromJar(getFileOfPath(folder, name), name);
						if (!good) {
							allGood = false;
						}
					}
				}
				jar.close();
			} else {
				final URL url = OSUtil.class.getResource(path);
				if (url != null) {
					final File apps = new File(url.toURI());
					for (File app : apps.listFiles()) {
						try {
							Files.copy(app.toPath(),
									new File(folder.getAbsolutePath() + File.separator + app.getName()).toPath(),
									StandardCopyOption.COPY_ATTRIBUTES);
						} catch (java.nio.file.FileAlreadyExistsException faex) {
						} catch (Exception ex) {
							allGood = false;
							Logger.log("Error while extracting file from folder from jar: " + ex, LogLevel.WARNING);
						}
					}
				} else {
					allGood = false;
				}
			}
			return allGood;
		} catch (Exception ex) {
			Logger.logErr("Error while extracting folder from jar: " + ex, ex);
			return false;
		}
	}

	public static final File getFileOfPath(File folder, String path) {
		String name = path;
		if (path.contains(PATHSEPARATOR)) {
			name = name.substring(name.lastIndexOf(PATHSEPARATOR) + PATHSEPARATOR.length());
		}
		return new File(folder.getAbsolutePath() + File.separator + name);
	}

	public static final File getJarFile() {
		return new File(OSUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	}

	public static final boolean isJarFile() {
		return getJarFile().isFile();
	}

	public static final boolean isIDE() {
		return !isJarFile();
	}

}
