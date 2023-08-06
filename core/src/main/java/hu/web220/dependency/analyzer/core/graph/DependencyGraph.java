package hu.web220.dependency.analyzer.core.graph;

import java.util.Map;
import java.util.TreeMap;

public class DependencyGraph {

    private final Map<String, DependencyNode> dependencyMap;

    private int printListCounter;

    private int creationCounter = 0;

    private int connectionCounter = 0;

    private ProjectNode projectNodeToPrint = null;

    private DependencyGraph() {
        dependencyMap = new TreeMap<>();
    }

    public void createProjectDependencyIfNotExists(String combinedId, String displayName) {
        if (notContainsDependency(combinedId)) {
            createProjectDependency(combinedId, displayName);
            increaseCreationCounter();
        }
    }

    private void createProjectDependency(String combinedId, String displayName) {
        NodeBuilder.create()
                .map(dependencyMap)
                .combinedId(combinedId)
                .projectName(displayName)
                .build();
    }

    private void increaseCreationCounter() {
        creationCounter++;
    }

    private boolean notContainsDependency(String combinedId) {
        return !dependencyMap.containsKey(combinedId);
    }

    public void createLibraryDependencyIfNotExists(String combinedId) {
        if (notContainsDependency(combinedId)) {
            createLibraryDependency(combinedId);
            increaseCreationCounter();
        }
    }

    private void createLibraryDependency(String combinedId) {
        NodeBuilder.create()
                .map(dependencyMap)
                .combinedId(combinedId)
                .build();
    }

    Map<String, DependencyNode> getDependencyMap() {
        return dependencyMap;
    }

    void increaseConnectionCounter() {
        connectionCounter++;
    }

    public void printStatistics() {
        System.out.printf(
                "Statistics: There are %d nodes and %d connections created in dependency graph.%n",
                creationCounter,
                connectionCounter);
    }

    public void printDependencyListInAscendingOrder() {
        restartPrintListCounter();
        loopThroughDependenciesToPrint();
    }

    private void restartPrintListCounter() {
        printListCounter = 1;
    }

    private void loopThroughDependenciesToPrint() {
        dependencyMap.forEach(this::printNodeBasedOnNodeType);
    }

    private void printNodeBasedOnNodeType(String combinedId, DependencyNode node) {
        getProjectNodeIfValid(node);
        if (isProjectNode()) {
            printProjectNode(combinedId);
        }
        else {
            printLibraryNode(combinedId);
        }
    }

    private void getProjectNodeIfValid(DependencyNode node) {
        projectNodeToPrint = node instanceof ProjectNode
                ? (ProjectNode) node : null;
    }

    private boolean isProjectNode() {
        return projectNodeToPrint != null;
    }

    private void printProjectNode(String combinedId) {
        System.out.printf("%d. %s (%s)%n",
                printListCounter++,
                projectNodeToPrint.getDisplayName(),
                combinedId);
    }

    private void printLibraryNode(String combinedId) {
        System.out.printf("%d. %s%n",
                printListCounter++,
                combinedId);
    }

    public static DependencyGraph create() {
        return new DependencyGraph();
    }
}
