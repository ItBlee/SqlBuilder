package com.itblee.model;

import java.io.Serializable;
import java.util.Arrays;

public class Code implements Serializable, CharSequence {

	private static final long serialVersionUID = -979229696925868743L;
	
	private final char[] value;

    public Code(CharSequence code) {
        this.value = code.toString().toCharArray();
    }

    public static Code valueOf(CharSequence code) {
        return new Code(code);
    }

    public static Code[] valueOf(CharSequence[] codes) {
        Code[] arr = new Code[codes.length];
        for (int i = 0; i < codes.length; i++)
            arr[i] = valueOf(codes[i]);
        return arr;
    }

    @Override
    public int length() {
        return value.length;
    }

    @Override
    public char charAt(int index) {
        return value[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return Arrays.toString(value).substring(start, end);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Code)) return false;
        Code that = (Code) o;
        return Arrays.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}
