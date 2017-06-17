package omnikryptec.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.util.WaveData;
import org.lwjgl.util.vector.Vector3f;

import omnikryptec.component.Component;
import omnikryptec.logger.Logger;

/**
 * Main audio manager class
 * 
 * @author Panzer1119
 */
public class AudioManager {

	private static final ArrayList<Sound> sounds = new ArrayList<>();
	private static DistanceModel distanceModel = null;
	private static boolean isInitialized = false;
	private static Component blockingAudioListenerComponent = null;

	/**
	 * Initializes the OpenAL AudioSystem
	 * 
	 * @return <tt>true</tt> if the AudioSystem was successfully initialized
	 */
	public static final boolean init() {
		if (isInitialized) {
			return false;
		}
		try {
			AL.create();
			setDistanceModel(DistanceModel.EXPONENT_CLAMPED);
			isInitialized = true;
			return true;
		} catch (Exception ex) {
			isInitialized = false;
			Logger.logErr("Error while initializing the sound library: " + ex, ex);
			return false;
		}
	}

	/**
	 * Sets the data for the listener of the AudioSystem
	 * 
	 * @param component
	 *            Component Component which sets the data
	 * @param position
	 *            Vector3f Vector of the position
	 * @param rotation
	 *            Vector3f Vector of the rotation
	 * @param velocity
	 *            Vector3f Vector of the velocity
	 * @return <tt>true</tt> if the data was successfully changed
	 */
	public static final boolean setListenerData(Component component, javax.vecmath.Vector3f position,
			javax.vecmath.Vector3f rotation, javax.vecmath.Vector3f velocity) {
		return setListenerData(component, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z,
				velocity.x, velocity.y, velocity.z);
	}

	/**
	 * Sets the data for the listener of the AudioSystem
	 * 
	 * @param component
	 *            Component Component which sets the data
	 * @param position
	 *            Vector3f Vector of the position
	 * @param rotation
	 *            Vector3f Vector of the rotation
	 * @param velocity
	 *            Vector3f Vector of the velocity
	 * @return <tt>true</tt> if the data was successfully changed
	 */
	public static final boolean setListenerData(Component component, Vector3f position, Vector3f rotation,
			Vector3f velocity) {
		return setListenerData(component, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z,
				velocity.x, velocity.y, velocity.z);
	}

	/**
	 * Sets the data for the listener of the AudioSystem
	 * 
	 * @param component
	 *            Component Component which sets the data
	 * @param posX
	 *            Float Float of the x-position
	 * @param posY
	 *            Float Float of the y-position
	 * @param posZ
	 *            Float Float of the z-position
	 * @param rotX
	 *            Float Float of the x-rotation
	 * @param rotY
	 *            Float Float of the y-rotation
	 * @param rotZ
	 *            Float Float of the z-rotation
	 * @param velX
	 *            Float Float of the x-velocity
	 * @param velY
	 *            Float Float of the y-velocity
	 * @param velZ
	 *            Float Float of the z-velocity
	 * @return <tt>true</tt> if the data was successfully changed
	 */
	public static final boolean setListenerData(Component component, float posX, float posY, float posZ, float rotX,
			float rotY, float rotZ, float velX, float velY, float velZ) {
		if (blockingAudioListenerComponent != null
				&& (component == null || blockingAudioListenerComponent != component)) {
			return false;
		}
		AL10.alListener3f(AL10.AL_POSITION, posX, posY, posZ);
		AL10.alListener3f(AL10.AL_ORIENTATION, rotX, rotY, rotZ);
		AL10.alListener3f(AL10.AL_VELOCITY, velX, velY, velZ);
		return true;
	}

	/**
	 * Sets the Component which blocks the setListenerData function
	 * 
	 * @param component
	 *            Component Active component or null
	 * @param newComponent
	 *            Component New component or null
	 * @return <tt>true</tt> if the component was set successfully
	 */
	public static final boolean setBlockingComponent(Component component, Component newComponent) {
		if (blockingAudioListenerComponent != null
				&& (component == null || blockingAudioListenerComponent != component)) {
			return false;
		}
		blockingAudioListenerComponent = newComponent;
		return true;
	}

	/**
	 * Loads a sound from a File to the static Soundbuffer
	 * 
	 * @param name
	 *            String Name of the Sound
	 * @param file
	 *            File File where the Sound is saved
	 * @return Integer BufferID where the sound was saved
	 * @throws FileNotFoundException
	 *             if the file was not found
	 */
	public static final int loadSound(String name, File file) throws FileNotFoundException {
		return loadSound(name, new FileInputStream(file));
	}

	/**
	 * Loads a sound from a Path within a jar to the static Soundbuffer
	 * 
	 * @param name
	 *            String Name of the Sound
	 * @param path
	 *            String Path in the jar where the Sound is saved
	 * @return Integer BufferID where the sound was saved
	 */
	public static final int loadSound(String name, String path) {
		return loadSound(name, AudioManager.class.getResourceAsStream(path));
	}

	/**
	 * Loads a sound from an InputStream to the static Soundbuffer
	 * 
	 * @param name
	 *            String Name of the Sound
	 * @param inputStream
	 *            InputStream Stream where should be read from
	 * @return Integer BufferID where the sound was saved
	 */
	public static final int loadSound(String name, InputStream inputStream) {
		deleteSound(name);
		final int bufferID = AL10.alGenBuffers();
		final WaveData waveData = WaveData.create(inputStream);
		AL10.alBufferData(bufferID, waveData.format, waveData.data, waveData.samplerate);
		waveData.dispose();
		final Sound sound = new Sound(name, bufferID);
		sounds.add(sound);
		return bufferID;
	}

	/**
	 * Returns a Sound for the given name
	 * 
	 * @param name
	 *            String Name of the Sound
	 * @return Sound Found Sound or null
	 */
	public static final Sound getSound(String name) {
		for (Sound sound : sounds) {
			if (sound.getName().equals(name)) {
				return sound;
			}
		}
		return null;
	}

	/**
	 * Returns all names of the loaded Sounds
	 * 
	 * @return String Array with all Sound names
	 */
	public static final String[] getSoundNames() {
		final String[] names = new String[sounds.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = sounds.get(i).getName();
		}
		return names;
	}

	/**
	 * Returns a Sound for the given bufferID
	 * 
	 * @param bufferID
	 *            Integer BufferID
	 * @return Sound Found Sound or null
	 */
	public static final Sound getSound(int bufferID) {
		for (Sound sound : sounds) {
			if (sound.getBufferID() == bufferID) {
				return sound;
			}
		}
		return null;
	}

	/**
	 * Deletes a Sound given by the name
	 * 
	 * @param name
	 *            String Name of the Sound to be deleted
	 * @return <tt>true</tt> if the Sound was found and deleted
	 */
	public static final boolean deleteSound(String name) {
		final Sound sound = getSound(name);
		if (sound != null) {
			boolean deleted = sound.delete(null);
			if (deleted) {
				sounds.remove(sound);
			}
			return deleted;
		} else {
			return false;
		}
	}

	/**
	 * Deletes a Sound given by the bufferID
	 * 
	 * @param bufferID
	 *            Integer BufferID
	 * @return <tt>true</tt> if the Sound was found and deleted
	 */
	public static final boolean deleteSound(int bufferID) {
		final Sound sound = getSound(bufferID);
		if (sound != null) {
			boolean deleted = sound.delete(null);
			if (deleted) {
				sounds.remove(sound);
			}
			return deleted;
		} else {
			return false;
		}
	}

	/**
	 * Cleans up every Sound and destroys the OpenAL AudioSystem
	 */
	public static final void cleanup() {
		for (AudioSource source : AudioSource.audioSources) {
			source.delete();
		}
		AudioSource.audioSources.clear();
		for (Sound sound : sounds) {
			sound.delete(null);
		}
		sounds.clear();
		AL.destroy();
	}

	/**
	 * Updates all StreamedSounds
	 * 
	 * @param currentTime
	 *            Long Current time in milliseconds
	 */
	public static final void update(long currentTime) {
		for (StreamedSound streamedSound : StreamedSound.streamedSounds) {
			streamedSound.update(currentTime);
		}
	}

	/**
	 * Returns if the OpenAL AudioSystem is initialized
	 * 
	 * @return <tt>true</tt> if the AudioSystem is initialized
	 */
	public static final boolean isInitialized() {
		return isInitialized;
	}

	/**
	 * Returns the used DistanceModel
	 * 
	 * @return DistanceModel DistanceModle used to calculate the volume of the
	 *         AudioSources
	 */
	public static final DistanceModel getDistanceModel() {
		return distanceModel;
	}

	/**
	 * Sets the used DistanceModel
	 * 
	 * @param distanceModel
	 *            DistanceModel DistanceModul that should be used to calculate
	 *            the volume of the AudioSources
	 */
	public static final void setDistanceModel(DistanceModel distanceModel) {
		if (distanceModel == null) {
			return;
		}
		AL10.alDistanceModel(distanceModel.getDistanceModel());
		AudioManager.distanceModel = distanceModel;
	}

	/**
	 * Distance model which calculates the roll off of the volume
	 */
	public static enum DistanceModel {
		EXPONENT(AL11.AL_EXPONENT_DISTANCE, false), EXPONENT_CLAMPED(AL11.AL_EXPONENT_DISTANCE_CLAMPED, true), INVERSE(
				AL10.AL_INVERSE_DISTANCE, false), INVERSE_CLAMPED(AL10.AL_INVERSE_DISTANCE_CLAMPED, true), LINEAR(
						AL11.AL_LINEAR_DISTANCE, false), LINEAR_CLAMPED(AL11.AL_LINEAR_DISTANCE_CLAMPED, true);

		private final int distanceModel;
		private final boolean clamped;

		/**
		 * Creates the DistanceModel
		 * 
		 * @param distanceModel
		 *            Integer OpenAL DistanceModel
		 * @param clamped
		 *            Boolean <tt>true</tt> if it is clamped
		 */
		DistanceModel(int distanceModel, boolean clamped) {
			this.distanceModel = distanceModel;
			this.clamped = clamped;
		}

		/**
		 * Returns the OpenAL-Equivalent for the DistanceModel
		 * 
		 * @return Integer OpenAL Distancemodel
		 */
		public final int getDistanceModel() {
			return distanceModel;
		}

		/**
		 * Returns if the DistanceModel is a clamped one
		 * 
		 * @return <tt>true</tt> if it is clamped
		 */
		public final boolean isClamped() {
			return clamped;
		}
	}

}
