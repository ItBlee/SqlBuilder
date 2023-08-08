package com.itblee.model;

import java.io.Serializable;

public class Range implements Serializable {
	private static final long serialVersionUID = 1703267770870712931L;
	public static final String RANGE_FROM = "from";
    public static final String RANGE_TO = "to";

    public final Number from;
    public final Number to;

    private Range(Number from, Number to) {
        this.from = from;
        this.to = to;
    }

    public static Range valueOf(Number from, Number to) {
        if ((from == null && to == null))
            throw new IllegalStateException();
        if (from != null && to != null && from.doubleValue() > to.doubleValue())
            throw new IllegalArgumentException("FROM value is greater than TO value");
        return new Range(from, to);
    }

}