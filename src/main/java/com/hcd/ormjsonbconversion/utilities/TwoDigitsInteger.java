package com.hcd.ormjsonbconversion.utilities;

public class TwoDigitsInteger {

    private final Integer value;

    public TwoDigitsInteger(Integer value) {
        this.value = value;
    }

    public boolean isValid() throws NotSetException {
        if (value == null) {
            throw new NotSetException("Number value not set.");
        }
        return value >= 10 && value <= 99;
    }

    public Integer getValue() throws NotSetException {
        if (value == null) {
            throw new NotSetException("Number value not set.");
        }
        return value;
    }
}
