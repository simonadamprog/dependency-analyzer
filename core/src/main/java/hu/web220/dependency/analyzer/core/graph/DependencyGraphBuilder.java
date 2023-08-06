package hu.web220.dependency.analyzer.core.graph;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.artifacts.ResolvedDependency;

public class DependencyGraphBuilder {

    private DependencyGraph dependencyGraph;

    private Project rootProject;

    private Project currentProject;

    private DependencyGraphBuilder() {}

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

    private String getProjectCombinedId(Project project) {
        return String.format(
                "%s:%s:%s",
                project.getGroup(),
                project.getName(),
                project.getVersion());
    }

    private void addSubprojectsToDependencyGraph() {
        rootProject.getSubprojects()
                .forEach(this::addProjectToDependencyGraph);
    }

    private void addLibrariesForAllProjectsAndSetConnections() {
        addLibrariesForProjectAndSetConnections(rootProject);
        loopThroughSubprojects();
    }

    private void addLibrariesForProjectAndSetConnections(Project project) {
        storeAsCurrentProject(project);
        loopThroughConfigurations();
    }

    private void storeAsCurrentProject(Project project) {
        currentProject = project;
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
                        addDependency(
                                getProjectCombinedId(currentProject),
                                dependency));
    }

    private void addDependency(String parentId, ResolvedDependency dependency) {
        createDependencyNode(dependency.getName());
        establishConnection(parentId, dependency.getName());
        loopThroughChildDependencies(dependency);
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
                addDependency(
                        dependency.getName(),
                        child));
    }

    private void loopThroughSubprojects() {
        rootProject.getSubprojects()
                .forEach(this::addLibrariesForProjectAndSetConnections);
    }

    public static DependencyGraphBuilder create() {
        return new DependencyGraphBuilder();
    }
}
