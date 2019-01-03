/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.old.net;

import java.io.Serializable;

/**
 * InputType
 * 
 * @author Panzer1119
 */
public enum InputType implements Serializable {
    MESSAGE_RECEIVED(0), CLIENT_LOGGED_IN(1), CLIENT_LOGGED_OUT(2), ANSWER(3), RAW_MESSAGE_RECEIVED(4), BROADCAST(5);

    /**
     * Value to serialize
     */
    private final int value;

    InputType(int value) {
	this.value = value;
    }

    /**
     * Returns the Value
     * 
     * @return Value
     */
    public final int getValue() {
	return value;
    }

    /**
     * Returns the InputType of a Value
     * 
     * @param value Value
     * @return InputType that matches the Value
     */
    public static final InputType ofValue(int value) {
	for (InputType inputType : values()) {
	    if (inputType.getValue() == value) {
		return inputType;
	    }
	}
	return null;
    }
}
