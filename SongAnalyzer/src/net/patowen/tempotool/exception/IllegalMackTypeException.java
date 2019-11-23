package net.patowen.tempotool.exception;

public class IllegalMackTypeException extends RuntimeException {
	private static final long serialVersionUID = 9192216926519295652L;
	
	private int type;
	
	public IllegalMackTypeException(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
}
