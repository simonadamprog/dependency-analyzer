package hu.simonadamprog.dependency.analyzer.core.util;

import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Project;

public class ParameterBuilder {

    public static final String PARAMETER_LIBRARY = "lib";
    public static final String PARAMETER_UNIQUE_LIST = "list";
    public static final String PARAMETER_STATISTICS = "stats";
    public static final String PARAMETER_CIRCULARITY = "circular";
    private static final String PARAMETER_ERROR_MESSAGE = "Parameter \"%s\" is mandatory.";

    private static final String VALUE_ERROR_MESSAGE = "Value for parameter \"%s\" is invalid.";

    private Project project;

    private String parameterKey;

    private int length;

    private boolean isMandatory;

    private String regularExpression;

    private String value;

    public static ParameterBuilder create() {
        return new ParameterBuilder();
    }

    private ParameterBuilder() {}

    public ParameterBuilder project(Project project) {
        this.project = project;
        return this;
    }

    public ParameterBuilder parameterKey(String parameterKey) {
        this.parameterKey = parameterKey;
        return this;
    }

    public ParameterBuilder abbreviateToLength(int length) {
        this.length = length;
        return this;
    }

    public ParameterBuilder isMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
        return this;
    }

    public ParameterBuilder regularExpression(String regularExpression) {
        this.regularExpression = regularExpression;
        return this;
    }

    public Parameter build() {
        if (!validateParam()) {
            return Parameter.createNotSetFlag();
        }
        getValue();
        abbreviateValue();
        validateValue();
        return Parameter.create(value);
    }

    private boolean validateParam() {
        if (!project.getProperties().containsKey(parameterKey)) {
            if (isMandatory) {
                throwException(PARAMETER_ERROR_MESSAGE);
            }
            else {
                return false;
            }
        }
        return true;
    }

    private void getValue() {
        value = project.getProperties()
                .get(parameterKey)
                .toString();
    }

    private void abbreviateValue() {
        value = StringUtils.abbreviate(value, length);
    }

    private void validateValue() {
        if (regularExpression != null && !value.matches(regularExpression)) {
            throwException(VALUE_ERROR_MESSAGE);
        }
    }

    private void throwException(String template) {
        String message = String.format(template, parameterKey);
        System.out.printf(message);
        throw new RuntimeException(message);
    }
}
