package omnikryptec.util.error;

public class ErrorObject<T> implements ErrorItem{
	
	private T object;
	private int insertIndex;
	
	public ErrorObject(T item){
		this(item, -1);
	}
	
	public ErrorObject(T item, int insertIndex){
		this.object = item;
		this.insertIndex = insertIndex;
	}
	
	public T getObject(){
		return object;
	}
	
	public int getInsertionIndex(){
		return insertIndex;
	}
	
	@Override
	public String toString(){
		return object.toString();
	}

	@Override
	public String getError() {
		return this.toString();
	}
	
}
