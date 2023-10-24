package hu.simonadamprog.dependency.analyzer.core.util;

import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Project;

public class ParameterBuilder {

    public static final String PARAMETER_LIBRARY = "lib";
    public static final String PARAMETER_UNIQUE_LIST = "list";
    public static final String PARAMETER_STATISTICS = "stats";
    public static final String PARAMETER_CIRCULARITY = "circular";
    private static final String PARAMETER_ERROR_MESSAGE = "Parameter \"%s\" is mandatory.";

    private Project project;

    private String parameterKey;

    private int length;

    private boolean isMandatory;

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

    public Parameter build() {
        if (!validateParam()) {
            return Parameter.createNotSetFlag();
        }
        String value = project.getProperties()
                .get(parameterKey).toString();
        return Parameter.create(StringUtils.abbreviate(value, length));
    }

    private boolean validateParam() {
        if (!project.getProperties().containsKey(parameterKey)) {
            if (isMandatory) {
                String message = String.format(PARAMETER_ERROR_MESSAGE, parameterKey);
                System.out.printf(message);
                throw new RuntimeException(message);
            } else {
                return false;
            }
        }
        return true;
    }
}
