package omnikryptec.event.eventV2;

@FunctionalInterface
public interface ClassFilter {

	boolean accept(Class<?> clazz);

}
