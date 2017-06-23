package omnikryptec.test.saving;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import omnikryptec.logger.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * XMLSerializer
 * @author Panzer1119
 */
public class XMLSerializer implements IDataMapSerializer {
    
    public static Format format = Format.getPrettyFormat();
    
    private final SAXBuilder saxBuilder = new SAXBuilder();
    private final XMLOutputter xmlOutput = new XMLOutputter();
    private Document document = null;
    private Element rootElement = null;

    @Override
    public final boolean serialize(String name, HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps, OutputStream outputStream) {
        try {
            rootElement = new Element(name);
            document = new Document(rootElement);
            for(Class<?> c : classesDataMaps.keySet()) {
                final Element element = new Element(Type.Class.name()).setAttribute(NAME, c.getName());
                final ArrayList<DataMap> dataMaps = classesDataMaps.get(c);
                for(DataMap dataMap : dataMaps) {
                    processDataMapsSerialize(element, dataMap.getName(), dataMap, 0);
                }
                rootElement.addContent(element);
            }
            xmlOutput.setFormat(format);
            xmlOutput.output(document, outputStream);
            outputStream.close();
            return true;
        } catch (Exception ex) {
            Logger.logErr("Error while serializing HashMap<Class<?>, ArrayList<DataMap>>: " + ex, ex);
            return false;
        }
    }
    
    private final void processDataMapsSerialize(Element element, Object name, DataMap dataMap, int depth) {
        final Element element_temp = new Element(Type.DataMap.name()).setAttribute(NAME, "" + name).setAttribute(COUNT, Type.DataMap.serializeKey(dataMap.size())).setAttribute(CLASS, String.class.getName()).setAttribute(DEPTH, "" + (depth - 1));
        for(String g : dataMap.keySet()) {
            final Object object = dataMap.get(g);
            if(object != null && object instanceof DataMap) {
                final DataMap dataMap_temp = (DataMap) object;
                processDataMapsSerialize(element_temp, g, dataMap_temp, depth + 1);
            } else if(object != null && object instanceof List) {
                final List list = (List) object;
                processListSerialize(element_temp, g, list, 0);
            } else if(object != null && object instanceof Map) {
                final Map map = (Map) object;
                processMapSerialize(element_temp, g, map, 0);
            } else {
                element_temp.addContent(new Element(Type.Data.name()).setText(Type.Data.serializeValue(object)).setAttribute(NAME, g).setAttribute(CLASS, getClassName(object)));
            }
        }
        element.addContent(element_temp);
    }
    
    private final void processListSerialize(Element element, Object name, List list, int depth) {
        final Element element_temp = new Element(Type.List.name()).setAttribute(NAME, "" + name).setAttribute(COUNT, Type.List.serializeKey(list.size())).setAttribute(CLASS, Integer.class.getName()).setAttribute(DEPTH, "" + (depth - 1));
        for(int i = 0; i < list.size(); i++) {
            final Object object = list.get(i);
            if(object != null && object instanceof DataMap) {
                final DataMap dataMap_temp = (DataMap) object;
                processDataMapsSerialize(element_temp, i, dataMap_temp, depth + 1);
            } else if(object != null && object instanceof List) {
                final List list_temp = (List) object;
                processListSerialize(element_temp, i, list_temp, depth + 1);
            } else if(object != null && object instanceof Map) {
                final Map map_temp = (Map) object;
                processMapSerialize(element_temp, i, map_temp, depth + 1);
            } else {
                element_temp.addContent(new Element(Type.ListData.name()).setText(Type.ListData.serializeValue(object)).setAttribute(Type.ListData.nameAttribute(), Type.ListData.serializeKey(i)).setAttribute(CLASS, getClassName(object)).setAttribute(DEPTH, "" + depth));
            }
        }
        element.addContent(element_temp);
    }
    
    private final void processMapSerialize(Element element, Object name, Map map, int depth) {
        Object temp = null;
        boolean foundType = true;
        final Element element_temp = new Element(Type.Map.name()).setAttribute(NAME, "" + name).setAttribute(COUNT, Type.Map.serializeKey(map.size())).setAttribute(DEPTH, "" + (depth - 1));
        for(Object key : map.keySet()) {
            if(foundType && temp != null && key != null && temp.getClass() != key.getClass()) {
                temp = null;
                foundType = false;
            }
            if(foundType) {
                temp = key;
            }
            final Object value = map.get(key);
            if(value != null && value instanceof DataMap) {
                final DataMap dataMap_temp = (DataMap) value;
                processDataMapsSerialize(element_temp, key, dataMap_temp, depth + 1);
            } else if(value != null && value instanceof List) {
                final List list_temp = (List) value;
                processListSerialize(element_temp, key, list_temp, depth + 1);
            } else if(value != null && value instanceof Map) {
                final Map map_temp = (Map) value;
                processMapSerialize(element_temp, key, map_temp, depth + 1);
            } else {
                element_temp.addContent(new Element(Type.MapData.name()).setText(Type.MapData.serializeValue(value)).setAttribute(Type.MapData.nameAttribute(), Type.MapData.serializeKey(key)).setAttribute(CLASS, getClassName(value)));
            }
        }
        element_temp.setAttribute(CLASS, getClassName(temp));
        element.addContent(element_temp);
    }
    
    private static final String getClassName(Object object) {
        if(object == null) {
            return Object.class.getName();
        }
        return object.getClass().getName();
    }

    @Override
    public final HashMap<Class<?>, ArrayList<DataMap>> unserialize(InputStream inputStream) {
        try {
            final HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps = new HashMap<>();
            document = saxBuilder.build(inputStream);
            rootElement = document.getRootElement();
            for(Element element : rootElement.getChildren()) {
                processClassUnserialize(element, classesDataMaps);
            }
            return classesDataMaps;
        } catch (Exception ex) {
            Logger.logErr("Error while unserializing InputStream: " + ex, ex);
            return null;
        }
    }
    
    private final void processClassUnserialize(Element element, HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps) {
        if(element.getName().equals(Type.Class.name())) {
            try {
                final Class<?> c = classForName(element.getAttributeValue(NAME));
                ArrayList<DataMap> dataMaps = classesDataMaps.get(c);
                if(dataMaps == null) {
                    dataMaps = new ArrayList<>();
                    classesDataMaps.put(c, dataMaps);
                }
                for(Element element_temp : element.getChildren()) {
                    final DataMap dataMap = new DataMap(element_temp.getAttributeValue(NAME));
                    processDataMapsUnserialize(element_temp, dataMap);
                    dataMaps.add(dataMap);
                }
            } catch (Exception ex) {
                Logger.logErr("Failed to resolve class for an element while unserializing: " + ex, ex);
            }
        }
    }
    
    private final void processDataMapsUnserialize(Element element, DataMap dataMap) {
        if(element.getName().equals(Type.DataMap.name())) {
            for(Element element_temp : element.getChildren()) {
                try {
                    processElementInDataMapUnserialize(element_temp, dataMap);
                } catch (Exception ex) {
                    Logger.logErr("Error while processing data maps while unserializing: " + ex, ex);
                }
            }
        }
    }
    
    private final void processElementInDataMapUnserialize(Element element, DataMap dataMap) {
        Class<?> c = null;
        if(element.getName().equals(Type.Data.name())) {
            c = classForName(element.getAttributeValue(CLASS));
            dataMap.put(element.getAttributeValue(NAME), cast(c, element.getText()));
        } else if(element.getName().equals(Type.DataMap.name())) {
            final DataMap dataMap_temp = new DataMap(element.getAttributeValue(NAME));
            processDataMapsUnserialize(element, dataMap_temp);
            dataMap.put(dataMap_temp.getName(), dataMap_temp);
        } else if(element.getName().equals(Type.List.name())) {
            final HashMap<Integer, Object> collectorMap = new HashMap<>();
            processListUnserialize(element, collectorMap);
            final List list = new ArrayList<>();
            collectorMap.keySet().stream().sorted((i1, i2) -> i2 - i1).forEach((i) -> {
                list.add(collectorMap.get(i));
            });
            collectorMap.clear();
            dataMap.put(element.getAttributeValue(NAME), list);
        } else if(element.getName().equals(Type.Map.name())) {
            final Map map = new HashMap();
            processMapUnserialize(element, map);
            dataMap.put(element.getAttributeValue(NAME), map);
        }
    }
    
    private final void processListUnserialize(Element element, HashMap<Integer, Object> collectorMap) {
        for(Element element_temp : element.getChildren()) {
            try {
                final Class<?> c = classForName(element_temp.getAttributeValue(CLASS));
                Integer index = -1;
                if(element_temp.getName().equals(Type.ListData.name())) {
                    index = Integer.parseInt(element_temp.getAttributeValue(Type.ListData.nameAttribute()));
                    collectorMap.put(index, cast(c, element_temp.getText()));
                } else if(element_temp.getName().equals(Type.DataMap.name())) {
                    index = Integer.parseInt(element_temp.getAttributeValue(NAME));
                    final DataMap dataMap_temp = new DataMap("" + index);
                    processDataMapsUnserialize(element_temp, dataMap_temp);
                    collectorMap.put(index, dataMap_temp);
                } else if(element_temp.getName().equals(Type.List.name())) {
                    index = Integer.parseInt(element_temp.getAttributeValue(NAME));
                    final HashMap<Integer, Object> collectorMap_temp = new HashMap<>();
                    processListUnserialize(element_temp, collectorMap_temp);
                    final List list = new ArrayList<>();
                    collectorMap_temp.keySet().stream().sorted((i1, i2) -> i2 - i1).forEach((i) -> {
                        list.add(collectorMap_temp.get(i));
                    });
                    collectorMap_temp.clear();
                    collectorMap.put(index, list);
                } else if(element_temp.getName().equals(Type.Map.name())) {
                    index = Integer.parseInt(element_temp.getAttributeValue(NAME));
                    final Map map = new HashMap();
                    processMapUnserialize(element_temp, map);
                    collectorMap.put(index, map);
                }
            } catch (Exception ex) {
                Logger.logErr("Error while processing list while unserializing: " + ex, ex);
            }
        }
    }
    
    private final void processMapUnserialize(Element element, Map map) {
        final Class<?> c_key = classForName(element.getAttributeValue(CLASS));
        for(Element element_temp : element.getChildren()) {
            try {
                final Class<?> c_value = classForName(element_temp.getAttributeValue(CLASS));
                Object key = null;
                if(element_temp.getName().equals(Type.MapData.name())) {
                    key = cast(c_key, element_temp.getAttributeValue(Type.MapData.nameAttribute()));
                    map.put(key, cast(c_value, element_temp.getText()));
                } else if(element_temp.getName().equals(Type.DataMap.name())) {
                    key = cast(c_key, element_temp.getAttributeValue(NAME));
                    final DataMap dataMap_temp = new DataMap("" + key);
                    processDataMapsUnserialize(element_temp, dataMap_temp);
                    map.put(key, dataMap_temp);
                } else if(element_temp.getName().equals(Type.List.name())) {
                    key = cast(c_key, element_temp.getAttributeValue(NAME));
                    final HashMap<Integer, Object> collectorMap = new HashMap<>();
                    processListUnserialize(element_temp, collectorMap);
                    final List list = new ArrayList<>();
                    collectorMap.keySet().stream().sorted((i1, i2) -> i2 - i1).forEach((i) -> {
                        list.add(collectorMap.get(i));
                    });
                    collectorMap.clear();
                    map.put(key, list);
                } else if(element_temp.getName().equals(Type.Map.name())) {
                    key = cast(c_key, element_temp.getAttributeValue(NAME));
                    final Map map_temp = new HashMap();
                    processMapUnserialize(element_temp, map_temp);
                    map.put(key, map_temp);
                }
            } catch (Exception ex) {
                Logger.logErr("Error while processing map while unserializing: " + ex, ex);
            }
        }
    }
    
    private final Class<?> classForName(String className) {
        try {
            return Class.forName(className);
        } catch (Exception ex) {
            Logger.logErr("Failed to resolve class for \"" + className + "\": " + ex, ex);
            return null;
        }
    }
    
    private final Object cast(Class<?> c, Object toCast) {
        if(c == null) {
            return toCast;
        }
        if(toCast instanceof String) {
            String temp = (String) toCast;
            if(c.equals(Long.class)) {
                return Long.parseLong(temp);
            } else if(c.equals(Float.class)) {
                return Float.parseFloat(temp);
            } else if(c.equals(Double.class)) {
                return Double.parseDouble(temp);
            } else if(c.equals(Integer.class)) {
                return Integer.parseInt(temp);
            } else if(c.equals(Short.class)) {
                return Short.parseShort(temp);
            } else if(c.equals(Boolean.class)) {
                return Boolean.parseBoolean(temp);
            } else if(c.equals(Byte.class)) {
                return Byte.parseByte(temp);
            } else if(c.equals(Character.class)) {
                if(temp.length() >= 1) {
                    return temp.charAt(0);
                } else {
                    return null;
                }
            }
        }
        return c.cast(toCast);
    }
    
    public static final String NAME = "name";
    public static final String COUNT = "count";
    public static final String CLASS = "class";
    public static final String DEPTH = "depth";
    
    public static enum Type {
        Class       ("", "", ""),
        Data        ("", "%s", ""),
        DataMap     ("%s", "", ""),
        List        ("%s", "", ""),
        ListData    ("%s", "%s", "index"),
        Map         ("", "", ""),
        MapData     ("%s", "%s", "key");
        
        private final String serializeKey;
        private final String serializeValue;
        private final String nameAttribute;
        
        Type(String serializeKey, String serializeValue, String nameAttribute) {
            this.serializeKey = serializeKey;
            this.serializeValue = serializeValue;
            this.nameAttribute = nameAttribute;
        }
        
        public String serializeKey(Object data) {
            return String.format(serializeKey, data);
        }
        
        public String serializeValue(Object data) {
            return String.format(serializeValue, data);
        }
        
        public String nameAttribute() {
            return nameAttribute;
        }
        
    }
    
    public static final XMLSerializer newInstance() {
        return new XMLSerializer();
    }
    
}
