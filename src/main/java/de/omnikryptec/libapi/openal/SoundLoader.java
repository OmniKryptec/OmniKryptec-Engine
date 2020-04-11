/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.libapi.openal;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class SoundLoader {

    private static void checkOpenAL() {
        if (!LibAPIManager.instance().isOpenALinitialized()) {
            throw new IllegalStateException("OpenAL is not initialized");
        }
    }

    public static final Sound loadSound(String name) {
        return loadSound(SoundLoader.class.getResourceAsStream(name));
    }

    public static final Sound loadSound(AdvancedFile file) {
        try {
            return loadSound(file.createInputStream());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static final Sound loadSound(InputStream inputStream) {
        checkOpenAL();
        try {
            //AudioSystem.getAudioInputStream(...) requires mark and reset
            if (!inputStream.markSupported()) {
                inputStream = new BufferedInputStream(inputStream);
            }
            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            if (audioInputStream == null) {
                throw new IllegalArgumentException("Couldn't create AudioInputStream");
            }
            final AudioFormat audioFormat = audioInputStream.getFormat();
            if (audioFormat.isBigEndian()) {
                throw new UnsupportedAudioFileException("Can not handle Big Endian formats yet"); // TODO Implement Big Endian formats
            }
            final byte[] bytes = IOUtils.toByteArray(audioInputStream);
            final ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
            data.flip();
            final Sound sound = new Sound(audioFormat, data);
            return sound;
        } catch (IOException | UnsupportedAudioFileException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static final StreamedSound streamSound(String name) {
        return streamSound(SoundLoader.class.getResourceAsStream(name));
    }

    public static final StreamedSound streamSound(AdvancedFile file) {
        try {
            return streamSound(file.createInputStream());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static final StreamedSound streamSound(InputStream stream) {
        checkOpenAL();
        try {
            //AudioSystem.getAudioInputStream(...) requires mark and reset
            if (!stream.markSupported()) {
                stream = new BufferedInputStream(stream);
            }
            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(stream);
            return new StreamedSound(audioInputStream);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates a new StreamSound of an InputStream
     *
     * @param inputStream InputStream Stream to load from
     * @param bufferCount Integer Number of used Buffers
     * @param bufferTime  Integer Buffered time in milliseconds
     * @return StreamedSound Sound
     */
    public static final StreamedSound streamSound(InputStream stream, int bufferCount, int bufferTime) {
        checkOpenAL();
        try {
            //AudioSystem.getAudioInputStream(...) requires mark and reset
            if (!stream.markSupported()) {
                stream = new BufferedInputStream(stream);
            }
            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(stream);
            return new StreamedSound(audioInputStream, bufferCount, bufferTime);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
