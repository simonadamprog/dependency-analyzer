package hu.web220.dependency.analyzer.core.util;

public class Parameter {

    private final boolean isSet;

    private final String value;

    public static Parameter createNotSetFlag() {
        return new Parameter(false, null);
    }

    public static Parameter create(String value) {
        return new Parameter(true, value);
    }

    public Parameter(boolean isSet, String value) {
        this.isSet = isSet;
        this.value = value;
    }

    public boolean isSet() {
        return isSet;
    }

    public String value() {
        return value;
    }
}
