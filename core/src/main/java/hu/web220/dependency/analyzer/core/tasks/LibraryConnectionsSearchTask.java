package hu.web220.dependency.analyzer.core.tasks;

import hu.web220.dependency.analyzer.core.DependencyAnalyzerPlugin;
import hu.web220.dependency.analyzer.core.graph.DependencyGraph;
import hu.web220.dependency.analyzer.core.graph.DependencyGraphBuilder;
import hu.web220.dependency.analyzer.core.util.LibraryIdBuilder;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.util.List;

public abstract class LibraryConnectionsSearchTask extends DefaultTask {

    private DependencyGraph dependencyGraph;

    private String libraryId;

    public LibraryConnectionsSearchTask() {
        setGroup(DependencyAnalyzerPlugin.TASK_GROUP);
    }

    @TaskAction
    public void perform() throws InterruptedException {
        loadLibraryProperty();
        buildDependencyGraph();
        printDependencyGraphDetails();
        printResult();
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
        System.out.printf("%n # # # # # %n%n");
    }

    private void printResult() {
        if (dependencyGraph.notContainsDependency(libraryId)) {
            System.out.printf("Given dependency: '%s' not exists.%n",
                    libraryId);
            return;
        }
        System.out.printf("Given dependency: '%s' exists.%n",
                libraryId);
        printSeparator();

        List<String> rootLibraries = dependencyGraph.getRootLibraryIds(libraryId);
        System.out.println("Root libraries are:");
        rootLibraries.forEach(this::printDependency);
        printSeparator();
        List<String> directProjectLibraries = dependencyGraph.getProjectParents(libraryId);
        System.out.println("Modules that contain this library directly:");
        directProjectLibraries.forEach(this::printDependency);
        printSeparator();
    }

    private void printDependency(String combinedId) {
        System.out.printf("   %s%n", combinedId);
    }
}
