package hu.web220.dependency.analyzer.core.display;

import java.util.List;

public class RootLibraryDetails {

    private final String libraryId;

    private final List<String> dependingModules;

    public RootLibraryDetails(final String libraryId, final List<String> dependingModules) {
        this.libraryId = libraryId;
        this.dependingModules = dependingModules;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public List<String> getDependingModules() {
        return dependingModules;
    }
}
