package hu.web220.dependency.analyzer.core.display;

import java.util.List;
import java.util.Set;

public class LibraryConnectionsSearchDisplay {

    private boolean isDisplayUniqueDependencies;

    private boolean isDisplayStatistics;

    private boolean isDisplayCircularDependencies;

    private String libraryId;

    private boolean containsDependency;

    private Set<List<String>> circularDependencies;

    private List<String> allDependencies;

    private List<String> rootLibraries;

    private List<String> directProjects;

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

    public LibraryConnectionsSearchDisplay rootLibraries(List<String> rootLibraries) {
        this.rootLibraries = rootLibraries;
        return this;
    }

    public LibraryConnectionsSearchDisplay directProjects(List<String> directProjects) {
        this.directProjects = directProjects;
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
        System.out.println("All unique dependencies are:");
    }

    private void loopThroughDependenciesToDisplay() {
        allDependencies.forEach(this::displayLineFromAllDependencies);
    }

    private void displayLineFromAllDependencies(String dependency) {
        System.out.printf("    %d. %s%n",
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
        System.out.printf(
                "Statistics: There are %d nodes and %d connections created in dependency graph.%n",
                nodeCount,
                connectionCount);
    }

    private void displayCircularDependenciesIfRequiredAndAny() {
        if (isDisplayCircularDependencies) {
            if (containsCircularDependency()) {
                displayCircularDependencies();
            }
            else {
                System.out.println("Circular dependencies are not found.");
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
        System.out.println("!!! Warning !!! Circular dependencies detected:");
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
        System.out.printf("    %d. %s%n",
                displayListCounter++,
                combinedCircularDependency);
    }

    private void displayLibraryId() {
        System.out.printf(
                "Given input (trimmed to 200 character length) is: %s%n",
                libraryId);
        displaySeparator();
    }

    private void displayExists() {
        System.out.printf("Library search from given input: %s%n", containsDependency ? "FOUND" : "NOT FOUND");
        displaySeparator();
    }

    private void displayConnectionsIfAnyExist() {
        if (containsDependency) {
            printRootLibraries();
            displayDirectProjectDependenciesIfAny();
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
        System.out.println("Found root Libraries are:");
    }

    private void displayRootLibraries() {
        rootLibraries.stream()
                .map(library -> {
                    if (library.equals(libraryId)){
                        return library + " (the searched library itself is a root Library)";
                    }
                    return library;
                })
                .forEach(this::displayDependency);
    }

    private void displayDirectProjectDependenciesIfAny() {
        if (isDisplayDirectProjectDependencies()) {
            displayDirectProjectsHeader();
            displayDirectProjects();
            displaySeparator();
        }
    }

    private boolean isDisplayDirectProjectDependencies() {
        return !directProjects.isEmpty();
    }

    private void displayDirectProjectsHeader() {
        System.out.println("Modules that contain this library directly:");
    }

    private void displayDirectProjects() {
        directProjects.forEach(this::displayDependency);
    }

    private void displayDependency(String combinedId) {
        System.out.printf("   %s%n", combinedId);
    }

    private void displaySeparator() {
        System.out.printf("%n # # # # # %n%n");
    }
}
