package hu.web220.dependency.analyzer.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Project;

public class LibraryIdBuilder {

    private static final String PARAMETER_LIBRARY = "library";
    private static final String PARAMETER_ERROR_MESSAGE = "Parameter \"" + PARAMETER_LIBRARY + "\" is not set properly.";

    private Project project;

    private LibraryIdBuilder() {}

    public LibraryIdBuilder project(Project project) {
        this.project = project;
        return this;
    }

    public String build() {
        if (!project.getProperties().containsKey(PARAMETER_LIBRARY)) {
            System.out.println(PARAMETER_ERROR_MESSAGE);
            throw new RuntimeException(PARAMETER_ERROR_MESSAGE);
        }
        String param = project.getProperties()
                .get(PARAMETER_LIBRARY).toString();
        String libraryId = StringUtils.abbreviate(param, 200);
        System.out.println("Given input trimmed to 200 character length is: " + libraryId);
        return libraryId;
    }

    public static LibraryIdBuilder create() {
        return new LibraryIdBuilder();
    }
}
