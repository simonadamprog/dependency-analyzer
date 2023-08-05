package hu.web220.dependency.analyzer.core.graph;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
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
        dependencyGraph.createProjectDependency(
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
        currentProject = project;
        loopThroughConfigurations();
    }

    private void loopThroughConfigurations() {
        currentProject.getConfigurations()
                .forEach(this::processResolvedLibrariesOfConfiguration);
    }

    private void processResolvedLibrariesOfConfiguration(Configuration configuration) {
        if (!configuration.isCanBeResolved()) {
            return;
        }
        configuration.getResolvedConfiguration()
                .getFirstLevelModuleDependencies()
                .forEach(dependency ->
                        addDependency(
                                dependency,
                                getProjectCombinedId(currentProject)));
    }

    private void addDependency(ResolvedDependency dependency, String parentId) {
        dependencyGraph.createLibraryDependency(dependency.getName());
        dependencyGraph.establishConnection(parentId, dependency.getName());
        loopThroughChildDependencies(dependency);
    }

    private void loopThroughChildDependencies(ResolvedDependency dependency) {
        dependency.getChildren().forEach(child ->
                addDependency(
                        child,
                        dependency.getName()));
    }

    private void loopThroughSubprojects() {
        rootProject.getSubprojects()
                .forEach(this::addLibrariesForProjectAndSetConnections);
    }

    public static DependencyGraphBuilder create() {
        return new DependencyGraphBuilder();
    }
}
