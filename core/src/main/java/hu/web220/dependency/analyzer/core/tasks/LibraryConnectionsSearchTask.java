package hu.web220.dependency.analyzer.core.tasks;

import hu.web220.dependency.analyzer.core.DependencyAnalyzerPlugin;
import hu.web220.dependency.analyzer.core.graph.DependencyGraph;
import hu.web220.dependency.analyzer.core.graph.DependencyGraphBuilder;
import hu.web220.dependency.analyzer.core.display.LibraryConnectionsSearchDisplay;
import hu.web220.dependency.analyzer.core.util.ParameterBuilder;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public abstract class LibraryConnectionsSearchTask extends DefaultTask {

    private DependencyGraph dependencyGraph;

    private String libraryId;


    public LibraryConnectionsSearchTask() {
        setGroup(DependencyAnalyzerPlugin.TASK_GROUP);
    }

    @TaskAction
    public void perform() {
        loadLibraryProperty();
        buildDependencyGraph();
        printDependencyGraphDetails();
    }

    private void loadLibraryProperty() {
        libraryId = ParameterBuilder
                .create()
                .project(getProject())
                .parameterKey(ParameterBuilder.PARAMETER_LIBRARY)
                .isMandatory(true)
                .abbreviateToLength(200)
                .build()
                .value();
    }

    private void buildDependencyGraph() {
        dependencyGraph = DependencyGraphBuilder
                .create()
                .rootProject(getProject().getRootProject())
                .build();
    }

    private void printDependencyGraphDetails() {
        LibraryConnectionsSearchDisplay
                .create()
                .logger(getLogger())
                .libraryId(libraryId)
                .isDisplayUniqueDependencies(isDisplayList())
                .allUniqueDependencies(dependencyGraph.getAllUniqueDependencies())
                .isDisplayCircularDependencies(isDisplayCircular())
                .containsDependency(dependencyGraph.containsDependency(libraryId))
                .circularDependencies(dependencyGraph.getCircularityStore())
                .rootLibrariesWithDependingModules(dependencyGraph.getRootLibraryDetails(libraryId))
                .isDisplayStatistics(isDisplayStats())
                .nodeCount(dependencyGraph.getCreationCounter())
                .connectionCount(dependencyGraph.getConnectionCounter())
                .display();
    }

    private boolean isDisplayList() {
        return ParameterBuilder
                .create()
                .project(getProject())
                .parameterKey(ParameterBuilder.PARAMETER_UNIQUE_LIST)
                .isMandatory(false)
                .abbreviateToLength(0)
                .build()
                .isSet();
    }

    private boolean isDisplayStats() {
        return ParameterBuilder
                .create()
                .project(getProject())
                .parameterKey(ParameterBuilder.PARAMETER_STATISTICS)
                .isMandatory(false)
                .abbreviateToLength(0)
                .build()
                .isSet();
    }

    private boolean isDisplayCircular() {
        return ParameterBuilder
                .create()
                .project(getProject())
                .parameterKey(ParameterBuilder.PARAMETER_CIRCULARITY)
                .isMandatory(false)
                .abbreviateToLength(0)
                .build()
                .isSet();
    }
}
