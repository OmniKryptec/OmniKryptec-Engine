package omnikryptec.shader.modules;

@FunctionalInterface
public interface DynamicAccess<T> {

	public T get();
}
