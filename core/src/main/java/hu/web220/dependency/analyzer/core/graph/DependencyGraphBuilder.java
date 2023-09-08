package hu.web220.dependency.analyzer.core.graph;

import hu.web220.dependency.analyzer.core.util.CircularityDetection;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.artifacts.ResolvedDependency;

import java.util.*;

public class DependencyGraphBuilder {

    private final Deque<String> circularDependencyDetectorStack;

    private List<String> marker;

    private DependencyGraph dependencyGraph;

    private Project rootProject;

    private Project currentProject;

    private String currentProjectId;

    private String currentParentId;

    private ResolvedDependency currentDependency;

    private String currentDependencyId;

    public static DependencyGraphBuilder create() {
        return new DependencyGraphBuilder();
    }

    private DependencyGraphBuilder() {
        circularDependencyDetectorStack = new ArrayDeque<>();

    }

    public DependencyGraphBuilder rootProject(Project rootProject) {
        this.rootProject = rootProject;
        return this;
    }

    public DependencyGraph build() {
        createPlainDependencyGraph();
        // Root Project and Subprojects are needed to be added first,
        // because resolved dependencies don't have a 'displayName' member.
        addRootProjectToDependencyGraph();
        addSubprojectsToDependencyGraph();
        addLibrariesForAllProjectsAndSetConnections();
        return dependencyGraph;
    }

    private void createPlainDependencyGraph() {
        dependencyGraph = DependencyGraph.create();
    }

    private void addRootProjectToDependencyGraph() {
        addProjectToDependencyGraph(rootProject);
    }

    private void addSubprojectsToDependencyGraph() {
        rootProject.getSubprojects()
                .forEach(this::addProjectToDependencyGraph);
    }

    private void addProjectToDependencyGraph(Project project) {
        dependencyGraph.createProjectDependencyIfNotExists(
                getProjectCombinedId(project),
                project.getDisplayName());
    }

    private void addLibrariesForAllProjectsAndSetConnections() {
        addLibrariesForRootProjectAndSetConnections();
        loopThroughSubprojects();
    }

    private void addLibrariesForRootProjectAndSetConnections() {
        addLibrariesForProjectAndSetConnections(rootProject);
    }

    private void loopThroughSubprojects() {
        rootProject.getSubprojects()
                .forEach(this::addLibrariesForProjectAndSetConnections);
    }

    private void addLibrariesForProjectAndSetConnections(Project project) {
        storeProjectParametersAsMember(project);
        pushProjectToCircularityCheck();
        loopThroughConfigurations();
        popProjectFromCircularityCheck();
    }

    private void storeProjectParametersAsMember(Project project) {
        currentProject = project;
        currentProjectId = getProjectCombinedId(project);
    }

    private String getProjectCombinedId(Project project) {
        return String.format(
                "%s:%s:%s",
                project.getGroup(),
                project.getName(),
                project.getVersion());
    }

    private void pushProjectToCircularityCheck() {
        circularDependencyDetectorStack.push(currentProjectId);
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
                        addDependenciesRecursivelyWithCircularityDetection(
                                currentProjectId,
                                dependency));
    }

    private void addDependenciesRecursivelyWithCircularityDetection(String parentId, ResolvedDependency dependency) {
        storeRecursiveParametersAsMembers(parentId, dependency);
        createNodeAndConnection();
        if (containsDependencyInStack()) {
            storeCircularDependencyMarkerIfNew();
        }
        else {
            loopThroughChildDependenciesWrappedByCircularityDetectionPart();
        }
    }

    private void storeRecursiveParametersAsMembers(String parentId, ResolvedDependency dependency) {
        currentParentId = parentId;
        currentDependency = dependency;
        currentDependencyId = dependency.getName();
    }

    private void createNodeAndConnection() {
        createDependencyNode();
        establishConnection();
    }

    private void createDependencyNode() {
        dependencyGraph.createLibraryDependencyIfNotExists(currentDependencyId);
    }

    private void establishConnection() {
        ConnectionBuilder.create()
                .dependencyGraph(dependencyGraph)
                .parentCombinedId(currentParentId)
                .childCombinedId(currentDependencyId)
                .build();
    }

    private boolean containsDependencyInStack() {
        return circularDependencyDetectorStack.contains(currentDependencyId);
    }

    private void storeCircularDependencyMarkerIfNew() {
        combineCircularDependencyList();
        if (isCircularDependencyNotStored()) {
            storeCircularDependencyMarker();
        }
    }

    private void combineCircularDependencyList() {
        marker = new ArrayList<>();
        List<String> list = new ArrayList<>(circularDependencyDetectorStack);
        Collections.reverse(list);
        boolean gather = false;
        for (String dependency : list) {
            if (dependency.equals(currentDependencyId)) {
                gather = true;
            }
            if (gather) {
                marker.add(dependency);
            }
        }
    }

    private boolean isCircularDependencyNotStored() {
        return dependencyGraph.circularityStore
                .stream()
                .noneMatch(circularity ->
                        CircularityDetection.isListCircularlyTheSame(circularity, marker));
    }

    private void storeCircularDependencyMarker() {
        dependencyGraph.circularityStore.add(marker);
    }

    private void loopThroughChildDependenciesWrappedByCircularityDetectionPart() {
        pushDependencyIdToStack();
        loopThroughChildDependencies(currentDependency);
        popDependencyIdFromStack();
    }

    private void pushDependencyIdToStack() {
        circularDependencyDetectorStack.push(currentDependencyId);
    }

    private void loopThroughChildDependencies(ResolvedDependency currentDependency) {
        currentDependency.getChildren().forEach(child ->
                addDependenciesRecursivelyWithCircularityDetection(
                        currentDependency.getName(),
                        child));
    }

    private void popDependencyIdFromStack() {
        circularDependencyDetectorStack.pop();
    }
}
