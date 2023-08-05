package hu.web220.dependency.analyzer.core.graph;

import java.util.TreeMap;

public class DependencyGraph {

    private final TreeMap<String, DependencyNode> searchableDependencyMap;

    private int printCounter;

    private int creationCounter = 0;

    private int connectionCounter = 0;

    private DependencyGraph() {
        searchableDependencyMap = new TreeMap<>();
    }

    public void createProjectDependency(String combinedId, String displayName) {
        if (containsDependency(combinedId)) {
            return;
        }

        NodeBuilder.create()
                .map(searchableDependencyMap)
                .combinedId(combinedId)
                .projectName(displayName)
                .build();

        creationCounter++;
    }

    private boolean containsDependency(String combinedId) {
        return searchableDependencyMap.containsKey(combinedId);
    }

    public void createLibraryDependency(String combinedId) {
        if (containsDependency(combinedId)) {
            return;
        }

        NodeBuilder.create()
                .map(searchableDependencyMap)
                .combinedId(combinedId)
                .build();

        creationCounter++;
    }
    public void establishConnection(String parentCombinedId, String childCombinedId) {
        DependencyNode parent = searchableDependencyMap.get(parentCombinedId);
        DependencyNode child = searchableDependencyMap.get(childCombinedId);

        if (child.containsParentDependency(parent)) {
            return;
        }

        parent.addChildDependency(child);
        child.addParentDependency(parent);

        connectionCounter++;
    }

    public void printData() {
        System.out.printf(
                "There are %d creations and %d connections%n",
                creationCounter,
                connectionCounter);
    }

    public void printList() {
        printCounter = 1;
        searchableDependencyMap.forEach((key, value) -> {
            String displayName = "";
            if (value instanceof ProjectNode) {
                displayName = ((ProjectNode) value).getDisplayName();
            }
            System.out.printf(
                    "%d. %s :: %s%n",
                    printCounter++,
                    displayName,
                    key);
        });
    }

    public static DependencyGraph create() {
        return new DependencyGraph();
    }
}
