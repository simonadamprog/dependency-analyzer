package hu.web220.dependency.analyzer.core.tasks;

import hu.web220.dependency.analyzer.core.DependencyAnalyzerPlugin;
import hu.web220.dependency.analyzer.core.graph.DependencyGraph;
import hu.web220.dependency.analyzer.core.graph.DependencyGraphBuilder;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public abstract class LibraryConnectionsSearchTask extends DefaultTask {
    private static final String PARAMETER_LIBRARY = "library";
    private static final String PARAMETER_ERROR_MESSAGE = "Parameter \"" + PARAMETER_LIBRARY + "\" is not set properly.";

    private DependencyGraph dependencyGraph;

    private String libraryId;

    public LibraryConnectionsSearchTask() {
        setGroup(DependencyAnalyzerPlugin.TASK_GROUP);
    }

    @TaskAction
    public void perform() {
        getLibraryProperty();
        buildDependencyGraph();
        printDependencyGraphDetails();

    }

    private void getLibraryProperty() {
        if (!getProject().getProperties().containsKey(PARAMETER_LIBRARY)) {
            System.out.println(PARAMETER_ERROR_MESSAGE);
            throw new RuntimeException(PARAMETER_ERROR_MESSAGE);
        }
        String param = getProject().getProperties()
                .get(PARAMETER_LIBRARY).toString();
        libraryId = StringUtils.abbreviate(param, 200);
        System.out.println("Given input trimmed to 200 character length is: " + libraryId);
    }

    private void buildDependencyGraph() {
        dependencyGraph = DependencyGraphBuilder
                .create()
                .rootProject(getProject().getRootProject())
                .build();
    }

    private void printDependencyGraphDetails() {
        dependencyGraph.printData();
        dependencyGraph.printList();
    }
}
