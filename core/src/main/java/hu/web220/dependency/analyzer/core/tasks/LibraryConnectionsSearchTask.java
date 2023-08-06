package hu.web220.dependency.analyzer.core.tasks;

import hu.web220.dependency.analyzer.core.DependencyAnalyzerPlugin;
import hu.web220.dependency.analyzer.core.graph.DependencyGraph;
import hu.web220.dependency.analyzer.core.graph.DependencyGraphBuilder;
import hu.web220.dependency.analyzer.core.utils.LibraryIdBuilder;
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
        libraryId = LibraryIdBuilder
                .create()
                .project(getProject())
                .build();
    }

    private void buildDependencyGraph() {
        dependencyGraph = DependencyGraphBuilder
                .create()
                .rootProject(getProject().getRootProject())
                .build();
    }

    private void printDependencyGraphDetails() {
        dependencyGraph.printStatistics();
        printSeparator();
        dependencyGraph.printDependencyListInAscendingOrder();
        printSeparator();
    }

    private void printSeparator() {
        System.out.println(" # # # # # ");
    }
}
