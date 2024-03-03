package hu.simonadamprog.dependency.analyzer.tasks;

import hu.simonadamprog.dependency.analyzer.DependencyAnalyzerPlugin;
import hu.simonadamprog.dependency.analyzer.graph.DependencyGraph;
import hu.simonadamprog.dependency.analyzer.graph.DependencyGraphBuilder;
import hu.simonadamprog.dependency.analyzer.display.LibraryConnectionsSearchDisplay;
import hu.simonadamprog.dependency.analyzer.util.BuildGradleFileUtil;
import hu.simonadamprog.dependency.analyzer.util.ParameterBuilder;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.time.Instant;

public abstract class LibraryConnectionsSearchTask extends DefaultTask {

    private static Instant dependencyGraphLastBuiltTimestamp = null;

    private static DependencyGraph dependencyGraph = null;

    private String libraryId;

    private boolean isBuiltInCurrentRun;


    public LibraryConnectionsSearchTask() {
        setGroup(DependencyAnalyzerPlugin.TASK_GROUP);
    }

    @TaskAction
    public void perform() {
        loadLibraryProperty();
        buildDependencyGraphIfNecessary();
        printDependencyGraphDetails();
    }

    private void loadLibraryProperty() {
        libraryId = ParameterBuilder
                .create()
                .project(getProject())
                .parameterKey(ParameterBuilder.PARAMETER_LIBRARY)
                .isMandatory(true)
                .abbreviateToLength(200)
                .regularExpression("^[a-zA-Z0-9.:\\-]+$")
                .build()
                .value();
    }

    private void buildDependencyGraphIfNecessary() {
        if (isBuildGraphNecessary()) {
            saveDependencyGraphLastBuiltTimestamp();
            buildDependencyGraph();
            setCurrentRunBuiltTheGraphFlag();
        }
    }

    private boolean isBuildGraphNecessary() {
        return dependencyGraphLastBuiltTimestamp == null ||
                BuildGradleFileUtil.isAnyBuildFileModifiedSince(
                        getProject().getRootProject(),
                        dependencyGraphLastBuiltTimestamp);
    }

    private void saveDependencyGraphLastBuiltTimestamp() {
        dependencyGraphLastBuiltTimestamp = Instant.now();
    }

    private void buildDependencyGraph() {
        dependencyGraph = DependencyGraphBuilder
                .create()
                .rootProject(getProject().getRootProject())
                .build();
    }

    private void setCurrentRunBuiltTheGraphFlag() {
        isBuiltInCurrentRun = true;
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
                .lastGraphBuildTimeStamp(dependencyGraphLastBuiltTimestamp)
                .isDependencyGraphRegeneratedLastTime(isBuiltInCurrentRun)
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
