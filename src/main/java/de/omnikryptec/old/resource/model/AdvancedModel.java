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

package de.omnikryptec.old.resource.model;

import de.omnikryptec.old.resource.loader.ResourceObject;
import de.omnikryptec.old.test.saving.DataMap;

/**
 * AdvancedModel
 *
 * @author Panzer1119
 */
public interface AdvancedModel extends ResourceObject {

    /**
     * Returns the model
     *
     * @return Model Model
     */
    public Model getModel();

    /**
     * Returns the material
     *
     * @return Material Material
     */
    public Material getMaterial();

    /**
     * Returns the Name
     *
     * @return String Name
     */
    @Override
    public String getName();

    /**
     * Deletes the Model
     * 
     * @return A reference to this AdvancedModel
     */
    @Override
    public AdvancedModel delete();

    /**
     * Copies this AdvancedModel
     * 
     * @return A copy of this AdvancedModel
     */
    public AdvancedModel copy();

    /**
     * Returns a representation of this object in a DataMap
     * 
     * @param data DataMap Data
     * @return DataMap Data
     */
    public DataMap toDataMap(DataMap data);

    /**
     * Loads values from a DataMap
     * 
     * @param data DataMap Data
     * @return A reference to this AdvancedModel
     */
    public AdvancedModel fromDataMap(DataMap data);

}
