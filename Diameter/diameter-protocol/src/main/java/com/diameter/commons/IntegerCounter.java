package com.diameter.commons;

public class IntegerCounter implements CircularCounter<Integer> {
	
	private int current;
	private int mod;
	
	public IntegerCounter() {
		this(Integer.MAX_VALUE);
	}
	
	public IntegerCounter(int mod) {
		this(-1, mod);
	}

	public IntegerCounter(int initial, int mod) {
		this.current = initial;
		this.mod = mod;
	}

	@Override
	public Integer next() {
		current = ((current + 1) % mod);
		return current;
	}
	
}