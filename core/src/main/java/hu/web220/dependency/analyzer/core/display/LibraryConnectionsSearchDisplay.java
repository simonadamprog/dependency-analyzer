package hu.web220.dependency.analyzer.core.display;

import org.gradle.api.logging.Logger;

import java.util.List;
import java.util.Set;

public class LibraryConnectionsSearchDisplay {

    private Logger log;

    private boolean isDisplayUniqueDependencies;

    private boolean isDisplayStatistics;

    private boolean isDisplayCircularDependencies;

    private String libraryId;

    private boolean containsDependency;

    private Set<List<String>> circularDependencies;

    private List<String> allDependencies;

    private List<RootLibraryDetails> rootLibraries;

    private int nodeCount;

    private int connectionCount;

    private int displayListCounter = 1;

    private String combinedCircularDependency;

    public static LibraryConnectionsSearchDisplay create() {
        return new LibraryConnectionsSearchDisplay();
    }

    private LibraryConnectionsSearchDisplay() {
    }

    public LibraryConnectionsSearchDisplay isDisplayUniqueDependencies(boolean idDisplayUniqueDependencies) {
        this.isDisplayUniqueDependencies = idDisplayUniqueDependencies;
        return this;
    }

    public LibraryConnectionsSearchDisplay isDisplayStatistics(boolean isDisplayStatistics) {
        this.isDisplayStatistics = isDisplayStatistics;
        return this;
    }

    public LibraryConnectionsSearchDisplay isDisplayCircularDependencies(boolean isDisplayCircularDependencies) {
        this.isDisplayCircularDependencies = isDisplayCircularDependencies;
        return this;
    }

    public LibraryConnectionsSearchDisplay logger(Logger logger) {
        this.log = logger;
        return this;
    }

    public LibraryConnectionsSearchDisplay libraryId(String libraryId) {
        this.libraryId = libraryId;
        return this;
    }

    public LibraryConnectionsSearchDisplay containsDependency(boolean containsDependency) {
        this.containsDependency = containsDependency;
        return this;
    }

    public LibraryConnectionsSearchDisplay circularDependencies(Set<List<String>> circularDependencies) {
        this.circularDependencies = circularDependencies;
        return this;
    }

    public LibraryConnectionsSearchDisplay allUniqueDependencies(List<String> allDependencies) {
        this.allDependencies = allDependencies;
        return this;
    }

    public LibraryConnectionsSearchDisplay rootLibrariesWithDependingModules(List<RootLibraryDetails> rootLibraries) {
        this.rootLibraries = rootLibraries;
        return this;
    }

    public LibraryConnectionsSearchDisplay nodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
        return this;
    }

    public LibraryConnectionsSearchDisplay connectionCount(int connectionCount) {
        this.connectionCount = connectionCount;
        return this;
    }

    public void display() {
        displayDependencyListInAscendingOrderIfRequired();
        displayStatisticsIfRequired();
        displayCircularDependenciesIfRequiredAndAny();
        displayLibraryId();
        displayExists();
        displayConnectionsIfAnyExist();
    }

    public void displayDependencyListInAscendingOrderIfRequired() {
        if (isDisplayUniqueDependencies) {
            displayAllDependenciesList();
        }
    }

    public void displayAllDependenciesList() {
        displayAllDependenciesHeader();
        restartDisplayListCounter();
        loopThroughDependenciesToDisplay();
        displaySeparator();
    }

    private void restartDisplayListCounter() {
        displayListCounter = 1;
    }

    private void displayAllDependenciesHeader() {
        log.quiet("All unique dependencies are:");
    }

    private void loopThroughDependenciesToDisplay() {
        allDependencies.forEach(this::displayLineFromAllDependencies);
    }

    private void displayLineFromAllDependencies(String dependency) {
        log.quiet("    {}. {}",
                displayListCounter++,
                dependency);
    }

    public void displayStatisticsIfRequired() {
        if (isDisplayStatistics) {
            displayStatistics();
            displaySeparator();
        }
    }

    public void displayStatistics() {
        log.quiet(
                "Statistics: There are {} nodes and {} connections created in dependency graph.",
                nodeCount,
                connectionCount);
    }

    private void displayCircularDependenciesIfRequiredAndAny() {
        if (isDisplayCircularDependencies) {
            if (containsCircularDependency()) {
                displayCircularDependencies();
            }
            else {
                log.quiet("Circular dependencies are not found.");
            }
            displaySeparator();
        }
    }

    private boolean containsCircularDependency() {
        return !circularDependencies.isEmpty();
    }

    private void displayCircularDependencies() {
        displayWarningMessage();
        restartDisplayListCounter();
        loopThroughCircularDependencyMarkers();
    }

    private void displayWarningMessage() {
        log.quiet("!!! Warning !!! Circular dependencies detected:");
    }

    private void loopThroughCircularDependencyMarkers() {
        circularDependencies.forEach(this::displayCircularDependency);
    }

    private void displayCircularDependency(List<String> circularDependencyMarker) {
        combineCircularDependencyMarkerToString(circularDependencyMarker);
        appendFirstElementToMarkerString(circularDependencyMarker);
        displayCombinedCircularDependencyMarker();
    }

    private void combineCircularDependencyMarkerToString(List<String> circularDependencyMarker) {
        combinedCircularDependency = String.join(" --> ", circularDependencyMarker);
    }

    private void appendFirstElementToMarkerString(List<String> circularDependencyMarker) {
        combinedCircularDependency += String.format(" --> %s", circularDependencyMarker.get(0));
    }

    private void displayCombinedCircularDependencyMarker() {
        log.quiet("    {}. {}",
                displayListCounter++,
                combinedCircularDependency);
    }

    private void displayLibraryId() {
        log.quiet("Given input (trimmed to 200 character length) is: {}",
                libraryId);
        displaySeparator();
    }

    private void displayExists() {
        log.quiet("Library search from given input: {}", containsDependency ? "FOUND" : "NOT FOUND");
        displaySeparator();
    }

    private void displayConnectionsIfAnyExist() {
        if (containsDependency) {
            printRootLibraries();
        }
    }

    private void printRootLibraries() {
        if (isThereAnyRootLibrary()) {
            displayRootLibrariesFoundMessage();
            displayRootLibraries();
            displaySeparator();
        }
    }

    private boolean isThereAnyRootLibrary() {
        return !rootLibraries.isEmpty();
    }

    private void displayRootLibrariesFoundMessage() {
        log.quiet("The found root libraries and the depending project modules are:");
    }

    private void displayRootLibraries() {
        rootLibraries.forEach(this::displayRootLibraryWithDependingModules);
    }

    private void displayRootLibraryWithDependingModules(RootLibraryDetails rootLibraryDetails) {
        log.quiet("    {}{}",
                rootLibraryDetails.getLibraryId(),
                textIfLibraryIsRootLibraryItself(rootLibraryDetails.getLibraryId()));
        rootLibraryDetails.getDependingModules().forEach(this::displayUserProject);
    }

    private String textIfLibraryIsRootLibraryItself(String libraryId) {
        return (libraryId.equals(this.libraryId))
                ? " (The searched library is itself a root library.)"
                : "";
    }

    private void displayUserProject(String userProject) {
        log.quiet("        {}", userProject);
    }

    private void displaySeparator() {
        log.quiet("{} # # # # # {}",
                System.lineSeparator(),
                System.lineSeparator());
    }
}
