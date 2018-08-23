package de.omnikryptec.util;

import org.lwjgl.openal.AL10;

import javax.sound.sampled.AudioFormat;

/**
 *
 * @author Panzer119
 */
public class AudioUtil {

	public static final int MONO = 1;
	public static final int STEREO = 2;

	public static final int audioFormatToOpenALFormat(AudioFormat audioFormat) {
		return audioFormatToOpenALFormat(audioFormat.getChannels(), audioFormat.getSampleSizeInBits());
	}

	public static final int audioFormatToOpenALFormat(int channels, int sampleSizeInBits) {
		int openALFormat = -1;
		switch (channels) {
		case MONO:
			switch (sampleSizeInBits) {
			case 8:
				openALFormat = AL10.AL_FORMAT_MONO8;
				break;
			case 16:
				openALFormat = AL10.AL_FORMAT_MONO16;
				break;
			}
			break;
		case STEREO:
			switch (sampleSizeInBits) {
			case 8:
				openALFormat = AL10.AL_FORMAT_STEREO8;
				break;
			case 16:
				openALFormat = AL10.AL_FORMAT_STEREO16;
				break;
			}
			break;
		}
		return openALFormat;
	}

}
