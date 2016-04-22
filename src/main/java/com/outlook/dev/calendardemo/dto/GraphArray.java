package com.outlook.dev.calendardemo.dto;

// Wrapper class for Graph calls that return arrays of objects
// The Graph puts that array in a field called "value"
// This is necessary to get Retrofit to parse the body properly
public class GraphArray<T> {
	private T[] value;

	public T[] getValue() {
		return value;
	}

	public void setValue(T[] value) {
		this.value = value;
	}
}
