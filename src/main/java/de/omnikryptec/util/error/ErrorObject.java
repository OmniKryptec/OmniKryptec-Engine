package de.omnikryptec.util.error;

public class ErrorObject<T> implements ErrorItem{
	
	private T object;
	
	public ErrorObject(T item){
		this.object = item;
	}
	
	public T getObject(){
		return object;
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
