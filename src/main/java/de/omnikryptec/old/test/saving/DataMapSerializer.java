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

package de.omnikryptec.old.test.saving;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import de.codemakers.util.ArrayUtil;
import de.omnikryptec.old.util.logger.Logger;

/**
 * DataMapSerializer
 *
 * @author Panzer1119
 */
public class DataMapSerializer {

    private HashMap<Class<?>, ArrayList<DataMapSerializable>> classesDataMapSerializables = new HashMap<>();
    private HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps = new HashMap<>();

    public static final HashMap<Class<?>, ArrayList<DataMap>> serialize(HashMap<Class<?>, ArrayList<DataMapSerializable>> classesDataMapSerializables) {
        if (classesDataMapSerializables == null) {
            return null;
        }
        final HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps = new HashMap<>();
        for (Class<?> c : classesDataMapSerializables.keySet()) {
            try {
                for (DataMapSerializable dataMapSerializable : classesDataMapSerializables.get(c)) {
                    try {
                        ArrayList<DataMap> data = classesDataMaps.get(c);
                        if (data == null) {
                            data = new ArrayList<>();
                            classesDataMaps.put(c, data);
                        }
                        data.add(dataMapSerializable.toDataMap(new DataMap("" + dataMapSerializable.getName())));
                    } catch (Exception ex) {
                        Logger.logErr(String.format("Error while serializing data from (inner) \"%s\": %s",
                                dataMapSerializable, ex), ex);
                    }
                }
            } catch (Exception ex) {
                Logger.logErr(String.format("Error while serializing data from (outer) \"%s\": %s", c.getName(), ex),
                        ex);
            }
        }
        return classesDataMaps;
    }

    public static final HashMap<Class<?>, ArrayList<DataMapSerializable>> deserialize(HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps) {
        if (classesDataMaps == null) {
            return null;
        }
        final HashMap<Class<?>, ArrayList<DataMapSerializable>> classesDataMapSerializables = new HashMap<>();
        for (Class<?> c : classesDataMaps.keySet()) {
            try {
                if (!ArrayUtil.contains(c.getInterfaces(), DataMapSerializable.class)) {
                    Logger.log(String.format("\"%s\" does not implement \"%s\"", c.getName(),
                            DataMapSerializable.class.getSimpleName()));
                    continue;
                }
                final ArrayList<DataMap> data = classesDataMaps.get(c);
                for (DataMap d : data) {
                    try {
                        final Object object = c.getMethod("newInstanceFromDataMap", d.getClass()).invoke(c.newInstance(), d);
                        if (object != null) {
                            addDataMapSerializable(classesDataMapSerializables, c, (DataMapSerializable) object);
                        } else {
                            Logger.log(String.format("Not deserialized (\"%s\"): \"%s\"", c.getSimpleName(), d));
                        }
                    } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
                        Logger.logErr(String.format("Error while unserializing data from (inner) \"%s\": %s", d, ex),
                                ex);
                    } catch (InvocationTargetException ex) {
                        Logger.logErr("Error: " + ex.getCause(), new Exception(ex.getCause()));
                    }
                }
            } catch (Exception ex) {
                Logger.logErr(String.format("Error while unserializing data from (outer) \"%s\": %s", c.getName(), ex),
                        ex);
            }
        }
        return classesDataMapSerializables;
    }

    public final HashMap<Class<?>, ArrayList<DataMap>> serialize() {
        return (classesDataMaps = serialize(classesDataMapSerializables));
    }

    public final HashMap<Class<?>, ArrayList<DataMapSerializable>> deserialize() {
        return (classesDataMapSerializables = deserialize(classesDataMaps));
    }

    public static final boolean serialize(String name, HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps, IDataMapSerializer dataMapSerializer, OutputStream outputStream) {
        if (classesDataMaps == null || dataMapSerializer == null || outputStream == null) {
            return false;
        }
        return dataMapSerializer.serialize(name, classesDataMaps, outputStream);
    }

    public final boolean serialize(String name, IDataMapSerializer dataMapSerializer, OutputStream outputStream) {
        return serialize(name, serialize(), dataMapSerializer, outputStream);
    }

    public static final HashMap<Class<?>, ArrayList<DataMap>> deserializeThisToDataMap(InputStream inputStream, IDataMapSerializer dataMapSerializer) {
        if (inputStream == null || dataMapSerializer == null) {
            return null;
        }
        return dataMapSerializer.deserialize(inputStream);
    }

    public static final HashMap<Class<?>, ArrayList<DataMapSerializable>> deserializeThisToDataMapSerializable(InputStream inputStream, IDataMapSerializer dataMapSerializer) {
        return deserialize(deserializeThisToDataMap(inputStream, dataMapSerializer));
    }

    public final HashMap<Class<?>, ArrayList<DataMap>> deserializeToDataMap(InputStream inputStream, IDataMapSerializer dataMapSerializer) {
        return (classesDataMaps = deserializeThisToDataMap(inputStream, dataMapSerializer));
    }

    public final HashMap<Class<?>, ArrayList<DataMapSerializable>> deserializeToDataMapSerializable(InputStream inputStream, IDataMapSerializer dataMapSerializer) {
        return (classesDataMapSerializables = deserializeThisToDataMapSerializable(inputStream, dataMapSerializer));
    }

    public static final HashMap<Class<?>, ArrayList<DataMapSerializable>> addDataMapSerializable(HashMap<Class<?>, ArrayList<DataMapSerializable>> classesDataMapSerializables, Class<?> c,
            DataMapSerializable dataMapSerializable) {
        ArrayList<DataMapSerializable> dataMapSerializables = classesDataMapSerializables.get(c);
        if (dataMapSerializables == null) {
            dataMapSerializables = new ArrayList<>();
            classesDataMapSerializables.put(c, dataMapSerializables);
        }
        dataMapSerializables.add(dataMapSerializable);
        return classesDataMapSerializables;
    }

    public final HashMap<Class<?>, ArrayList<DataMapSerializable>> addDataMapSerializable(Class<?> c, DataMapSerializable dataMapSerializable) {
        return DataMapSerializer.addDataMapSerializable(classesDataMapSerializables, c, dataMapSerializable);
    }

    public static final void addObject(HashMap<Class<?>, ArrayList<DataMapSerializable>> classesDataMapSerializables, Object object) {
        DataMapSerializer.addDataMapSerializable(classesDataMapSerializables, object.getClass(),
                (DataMapSerializable) object);
    }

    public final void addObject(Object object) {
        addDataMapSerializable(object.getClass(), (DataMapSerializable) object);
    }

    public static final <T> ArrayList<T> getObjects(HashMap<Class<?>, ArrayList<DataMapSerializable>> classesDataMapSerializables, Class<? extends T> type) {
        return (ArrayList<T>) (ArrayList<?>) classesDataMapSerializables.get(type);
    }

    public final <T> ArrayList<T> getObjects(Class<? extends T> type) {
        return getObjects(classesDataMapSerializables, type);
    }

    public static final ArrayList<DataMap> getDataMaps(HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps, Class<?> type, boolean onlySame) {
        if (onlySame) {
            return classesDataMaps.get(type);
        } else {
            final ArrayList<DataMap> dataMaps = new ArrayList<>();
            classesDataMaps.keySet().stream().forEach((c) -> {
                if (/*c.isAssignableFrom(type) || */type.isAssignableFrom(c)) { //FIXME Das auskommentierte kann eigentlich weg, aber noch nicht, falls es wichtig ist
                    dataMaps.addAll(classesDataMaps.get(c));
                }
            });
            return dataMaps;
        }
    }

    public static final ArrayList<DataMap> getDataMaps(HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps, Class<?> type) {
        return getDataMaps(classesDataMaps, type, true);
    }

    public final ArrayList<DataMap> getDataMaps(Class<?> type, boolean onlySame) {
        return getDataMaps(classesDataMaps, type, onlySame);
    }

    public final ArrayList<DataMap> getDataMaps(Class<?> type) {
        return getDataMaps(classesDataMaps, type, true);
    }

    public final HashMap<Class<?>, ArrayList<DataMapSerializable>> getClassesDataMapSerializables() {
        return classesDataMapSerializables;
    }

    public final HashMap<Class<?>, ArrayList<DataMap>> getClassesDataMaps() {
        return classesDataMaps;
    }

    public final DataMapSerializer setClassesDataMapSerializables(
            HashMap<Class<?>, ArrayList<DataMapSerializable>> classesDataMapSerializables) {
        this.classesDataMapSerializables = classesDataMapSerializables;
        return this;
    }

    public final DataMapSerializer setClassesDataMaps(HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps) {
        this.classesDataMaps = classesDataMaps;
        return this;
    }

    public final DataMapSerializer reset() {
        if (this.classesDataMapSerializables != null) {
            this.classesDataMapSerializables.clear();
        }
        if (this.classesDataMaps != null) {
            this.classesDataMaps.clear();
        }
        return this;
    }

}
