package omnikryptec.test;

/**
 *
 * @author Panzer1119
 */
public class TestObject implements Saveable {
    
    public String name = "";
    
    public TestObject(String name) {
        this.name = name;
    }

    @Override
    public Object[] toData() {
        return new Object[] {"name", name};
    }
    
}
