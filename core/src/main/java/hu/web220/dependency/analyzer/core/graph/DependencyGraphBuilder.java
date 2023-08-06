package hu.web220.dependency.analyzer.core.graph;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.artifacts.ResolvedDependency;

import java.util.*;

public class DependencyGraphBuilder {

    private final Set<String> circularDependenciesDetected;

    private final Stack<String> circularDependencyDetectorStack;

    private DependencyGraph dependencyGraph;

    private Project rootProject;

    private Project currentProject;

    private DependencyGraphBuilder() {
        circularDependenciesDetected = new HashSet<>();
        circularDependencyDetectorStack = new Stack<>();

    }

    public DependencyGraphBuilder rootProject(Project rootProject) {
        this.rootProject = rootProject;
        return this;
    }

    public DependencyGraph build() {
        createPlainDependencyGraph();
        // Root Project and Subprojects are needed to be added first,
        // because resolved dependencies don't have a 'displayName' member.
        addProjectToDependencyGraph(rootProject);
        addSubprojectsToDependencyGraph();
        addLibrariesForAllProjectsAndSetConnections();
        displayErrorsIfAny();
        return dependencyGraph;
    }

    private void createPlainDependencyGraph() {
        dependencyGraph = DependencyGraph.create();
    }

    private void addProjectToDependencyGraph(Project project) {
        dependencyGraph.createProjectDependencyIfNotExists(
                getProjectCombinedId(project),
                project.getDisplayName());
    }

    private void addSubprojectsToDependencyGraph() {
        rootProject.getSubprojects()
                .forEach(this::addProjectToDependencyGraph);
    }

    private void addLibrariesForAllProjectsAndSetConnections() {
        addLibrariesForProjectAndSetConnections(rootProject);
        loopThroughSubprojects();
    }

    private void displayErrorsIfAny() {
        if (containsCircularDependency()) {
            displayCircularDependencies();
        }
    }

    private void addLibrariesForProjectAndSetConnections(Project project) {
        storeAsCurrentProject(project);
        pushProjectToCircularityCheck();
        loopThroughConfigurations();
        popProjectFromCircularityCheck();
    }

    private String getProjectCombinedId(Project project) {
        return String.format(
                "%s:%s:%s",
                project.getGroup(),
                project.getName(),
                project.getVersion());
    }

    private void storeAsCurrentProject(Project project) {
        currentProject = project;
    }

    private void pushProjectToCircularityCheck() {
        circularDependencyDetectorStack.push(
                getProjectCombinedId(currentProject)
        );
    }

    private void popProjectFromCircularityCheck() {
        circularDependencyDetectorStack.pop();
    }

    private void loopThroughConfigurations() {
        currentProject.getConfigurations()
                .forEach(this::processLibrariesIfConfigurationResolvable);
    }

    private void processLibrariesIfConfigurationResolvable(Configuration configuration) {
        if (configuration.isCanBeResolved()) {
            processLibrariesRecursivelyOfResolvedConfiguration(
                    configuration.getResolvedConfiguration());
        }
    }

    private void processLibrariesRecursivelyOfResolvedConfiguration(ResolvedConfiguration configuration) {
        configuration
                .getFirstLevelModuleDependencies()
                .forEach(dependency ->
                        addDependencyWrappedByCircularityDetection(
                                getProjectCombinedId(currentProject),
                                dependency));
    }

    private void addDependencyWrappedByCircularityDetection(String parentId, ResolvedDependency dependency) {
        createNodeAndConnection(parentId, dependency);
        if (circularDependencyDetectorStack.contains(dependency.getName())) {
            storeCircularDependencyMarker(parentId, dependency.getName());
        }
        else {
            circularDependencyDetectorStack.push(dependency.getName());
            loopThroughChildDependencies(dependency);
            circularDependencyDetectorStack.pop();
        }
    }

    private void createNodeAndConnection(String parentId, ResolvedDependency dependency) {
        createDependencyNode(dependency.getName());
        establishConnection(parentId, dependency.getName());
    }

    private void createDependencyNode(String combinedId) {
        dependencyGraph.createLibraryDependencyIfNotExists(combinedId);
    }

    private void establishConnection(String parentId, String childId) {
        ConnectionBuilder.create()
                .dependencyGraph(dependencyGraph)
                .parentCombinedId(parentId)
                .childCombinedId(childId)
                .build();
    }

    private void loopThroughChildDependencies(ResolvedDependency dependency) {
        dependency.getChildren().forEach(child ->
                addDependencyWrappedByCircularityDetection(
                        dependency.getName(),
                        child));
    }

    private void storeCircularDependencyMarker(String parentId, String childId) {
        circularDependenciesDetected.add(
                combineCircularDependencyMarker(parentId, childId));
    }

    private String combineCircularDependencyMarker(String parentId, String childId) {
        return String.format(
                "%s --> %s",
                parentId,
                childId);
    }

    private void loopThroughSubprojects() {
        rootProject.getSubprojects()
                .forEach(this::addLibrariesForProjectAndSetConnections);
    }

    private boolean containsCircularDependency() {
        return !circularDependenciesDetected.isEmpty();
    }

    private void displayCircularDependencies() {
        printWarningMessage();
        loopThroughCircularDependencies();
    }

    private void printWarningMessage() {
        System.out.println("!!! Warning !!! Circular dependencies detected:");
    }

    private void loopThroughCircularDependencies() {
        circularDependenciesDetected.forEach(this::printCircularDependency);
    }

    private void printCircularDependency(String circularDependencyMarker) {
        System.out.printf("    %s%n", circularDependencyMarker);
    }

    public static DependencyGraphBuilder create() {
        return new DependencyGraphBuilder();
    }
}
