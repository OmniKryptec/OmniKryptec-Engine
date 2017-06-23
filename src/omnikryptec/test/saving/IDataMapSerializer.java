package omnikryptec.test.saving;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * IDataMapSerializer
 * @author Panzer1119
 */
public interface IDataMapSerializer {
    
    public boolean serialize(String name, HashMap<Class<?>, ArrayList<DataMap>> classesDataMaps, OutputStream outputStream);
    public HashMap<Class<?>, ArrayList<DataMap>> unserialize(InputStream inputStream);
    
}
