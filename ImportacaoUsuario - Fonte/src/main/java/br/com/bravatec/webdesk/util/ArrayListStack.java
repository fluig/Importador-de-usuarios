package br.com.bravatec.webdesk.util;

import java.util.ArrayList;

public class ArrayListStack<E> extends ArrayList<E> {
	/** */
	private static final long serialVersionUID = 1L;
	
	public void push( E item) {
		this.add(item);
	}
	
	public E peek() {
		return this.get(this.size() -1);
	}
	
	public E pop() {
		return this.remove(this.size() -1);
	}	
}
