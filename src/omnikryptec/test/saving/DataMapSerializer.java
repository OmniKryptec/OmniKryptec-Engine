package omnikryptec.test.saving;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import omnikryptec.logger.Logger;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.ArrayUtil;

/**
 * DataMapSerializer
 * @author Panzer1119
 */
public class DataMapSerializer {
    
    private HashMap<Class<?>, ArrayList<DataMapSerializable>> classesDataMapSerializables = new HashMap<>();
    private HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps = new HashMap<>();
    
    public static final HashMap<Class<?>, ArrayList<DataMap>> serialize(HashMap<Class<?>, ArrayList<DataMapSerializable>> classesDataMapSerializables) {
        if(classesDataMapSerializables == null) {
            return null;
        }
        final HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps = new HashMap<>();
        for(Class<?> c : classesDataMapSerializables.keySet()) {
            try {
                for(DataMapSerializable dataMapSerializable : classesDataMapSerializables.get(c)) {
                    try {
                        ArrayList<DataMap> data = classesDataMaps.get(c);
                        if(data == null) {
                            data = new ArrayList<>();
                            classesDataMaps.put(c, data);
                        }
                        data.add(dataMapSerializable.toDataMap(new DataMap("" + dataMapSerializable.getName())));
                    } catch (Exception ex) {
                        Logger.logErr(String.format("Error while serializing data from (inner) \"%s\": %s", dataMapSerializable, ex), ex);
                    }
                }
            } catch (Exception ex) {
                Logger.logErr(String.format("Error while serializing data from (outer) \"%s\": %s", c.getName(), ex), ex);
            }
        }
        return classesDataMaps;
    }
    
    public static final HashMap<Class<?>, ArrayList<DataMapSerializable>> unserialize(HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps) {
        if(classesDataMaps == null) {
            return null;
        }
        final HashMap<Class<?>, ArrayList<DataMapSerializable>> classesDataMapSerializables = new HashMap<>();
        for(Class<?> c : classesDataMaps.keySet()) {
            try {
                if(!ArrayUtil.contains(c.getInterfaces(), DataMapSerializable.class)) {
                    Logger.log(String.format("\"%s\" does not implement \"%s\"", c.getName(), DataMapSerializable.class.getSimpleName()));
                    continue;
                }
                final ArrayList<DataMap> data = classesDataMaps.get(c);
                for(DataMap d : data) {
                    try {
                        final Object object = c.getDeclaredMethod("newInstancefromDataMap", d.getClass()).invoke(c.newInstance(), d);
                        if(object != null) {
                            addDataMapSerializable(classesDataMapSerializables, c, (DataMapSerializable) object);
                        } else {
                            Logger.log(String.format("Not unserialized (\"%s\"): \"%s\"", c.getSimpleName(), d));
                        }
                    } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
                        Logger.logErr(String.format("Error while unserializing data from (inner) \"%s\": %s", d, ex), ex);
                    } catch (InvocationTargetException ex) {
                        Logger.logErr("Error: " + ex.getCause(), new Exception(ex.getCause()));
                    }
                }
            } catch (Exception ex) {
                Logger.logErr(String.format("Error while unserializing data from (outer) \"%s\": %s", c.getName(), ex), ex);
            }
        }
        return classesDataMapSerializables;
    }
    
    public final HashMap<Class<?>, ArrayList<DataMap>> serialize() {
        return (classesDataMaps = serialize(classesDataMapSerializables));
    }
    
    public final HashMap<Class<?>, ArrayList<DataMapSerializable>> unserialize() {
        return (classesDataMapSerializables = unserialize(classesDataMaps));
    }
    
    public static final boolean serialize(String name, HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps, IDataMapSerializer dataMapSerializer, AdvancedFile file) {
        if(classesDataMaps == null || dataMapSerializer == null || file == null || !file.createFile()) {
            return false;
        }
        return dataMapSerializer.serialize(name, classesDataMaps, file.createOutputstream(false));
    }
    
    public final boolean serialize(String name, IDataMapSerializer dataMapSerializer, AdvancedFile file) {
        return serialize(name, serialize(), dataMapSerializer, file);
    }
    
    public static final HashMap<Class<?>, ArrayList<DataMap>> unserializeThisToDataMap(AdvancedFile file, IDataMapSerializer dataMapSerializer) {
        if(file == null || !file.exists() || dataMapSerializer == null) {
            return null;
        }
        return dataMapSerializer.unserialize(file.createInputStream());
    }
    
    public static final HashMap<Class<?>, ArrayList<DataMapSerializable>> unserializeThisToDataMapSerializable(AdvancedFile file, IDataMapSerializer dataMapSerializer) {
        return unserialize(unserializeThisToDataMap(file, dataMapSerializer));
    }
    
    public final HashMap<Class<?>, ArrayList<DataMap>> unserializeToDataMap(AdvancedFile file, IDataMapSerializer dataMapSerializer) {
        return (classesDataMaps = unserializeThisToDataMap(file, dataMapSerializer));
    }
    
    public final HashMap<Class<?>, ArrayList<DataMapSerializable>> unserializeToDataMapSerializable(AdvancedFile file, IDataMapSerializer dataMapSerializer) {
        return (classesDataMapSerializables = unserializeThisToDataMapSerializable(file, dataMapSerializer));
    }
    
    public static final HashMap<Class<?>, ArrayList<DataMapSerializable>> addDataMapSerializable(HashMap<Class<?>, ArrayList<DataMapSerializable>> classesDataMapSerializables, Class<?> c, DataMapSerializable dataMapSerializable) {
        ArrayList<DataMapSerializable> dataMapSerializables = classesDataMapSerializables.get(c);
        if(dataMapSerializables == null) {
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
        DataMapSerializer.addDataMapSerializable(classesDataMapSerializables, object.getClass(), (DataMapSerializable) object);
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
    
    public static final ArrayList<DataMap> getDataMaps(HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps, Class<?> type) {
        return classesDataMaps.get(type);
    }
    
    public final ArrayList<DataMap> getDataMaps(Class<?> type) {
        return getDataMaps(classesDataMaps, type);
    }

    public final HashMap<Class<?>, ArrayList<DataMapSerializable>> getClassesDataMapSerializables() {
        return classesDataMapSerializables;
    }

    public final HashMap<Class<?>, ArrayList<DataMap>> getClassesDataMaps() {
        return classesDataMaps;
    }

    public final DataMapSerializer setClassesDataMapSerializables(HashMap<Class<?>, ArrayList<DataMapSerializable>> classesDataMapSerializables) {
        this.classesDataMapSerializables = classesDataMapSerializables;
        return this;
    }

    public final DataMapSerializer setClassesDataMaps(HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps) {
        this.classesDataMaps = classesDataMaps;
        return this;
    }
    
    public final DataMapSerializer reset() {
        if(this.classesDataMapSerializables != null) {
            this.classesDataMapSerializables.clear();
        }
        if(this.classesDataMaps != null) {
            this.classesDataMaps.clear();
        }
        return this;
    }
    
}
