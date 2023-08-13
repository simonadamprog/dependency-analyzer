package hu.web220.dependency.analyzer.core.graph;

import java.util.*;
import java.util.stream.Collectors;

public class DependencyGraph {

    private final Map<String, DependencyNode> dependencyMap;

    final Set<List<String>> circularityStore;

    private int creationCounter = 0;

    private int connectionCounter = 0;

    public static DependencyGraph create() {
        return new DependencyGraph();
    }

    private DependencyGraph() {
        dependencyMap = new TreeMap<>();
        circularityStore = new HashSet<>();
    }

    public int getCreationCounter() {
        return creationCounter;
    }

    public int getConnectionCounter() {
        return connectionCounter;
    }

    public Set<List<String>> getCircularityStore() {
        return circularityStore
                .stream()
                .map(ArrayList::new)
                .collect(Collectors.toSet());
    }

    public List<String> getAllUniqueDependencies() {
        return dependencyMap.values()
                .stream()
                .map(this::nodeToStringBasedOnNodeType)
                .sorted()
                .collect(Collectors.toList());
    }

    private String nodeToStringBasedOnNodeType(DependencyNode node) {
        if (isProjectNode(node)) {
            return projectNodeToString(node);
        }
        else {
            return node.getCombinedId();
        }
    }

    private boolean isProjectNode(DependencyNode node) {
        return node instanceof ProjectNode;
    }

    private String projectNodeToString(DependencyNode node) {
        ProjectNode projectNode = (ProjectNode) node;
        return String.format(":: %s (%s)",
                projectNode.getDisplayName(),
                projectNode.getCombinedId());
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

    public boolean containsDependency(String combinedId) {
        return dependencyMap.containsKey(combinedId);
    }

    public boolean notContainsDependency(String combinedId) {
        return !containsDependency(combinedId);
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

    public List<String> getRootLibraryIds(String libraryId) {
        if (notContainsDependency(libraryId)) {
            return Collections.emptyList();
        }
        DependencyNode node = dependencyMap.get(libraryId);
        List<String> list = getRootLibrariesRecursively(node);
        Collections.sort(list);
        return list;
    }

    private List<String> getRootLibrariesRecursively(DependencyNode node) {
        if (node instanceof ProjectNode) {
            return Collections.emptyList();
        }
        List<String> rootLibraryIds = new ArrayList<>();
        node.parentDependencies.forEach(parent ->
            rootLibraryIds.addAll(getRootLibrariesRecursively(parent)));
        if (rootLibraryIds.isEmpty()) {
            rootLibraryIds.add(node.combinedId);
        }
        return rootLibraryIds;
    }

    public List<String> getProjectParents(String libraryId) {
        if (notContainsDependency(libraryId)) {
            return Collections.emptyList();
        }
        DependencyNode node = dependencyMap.get(libraryId);
        return node.parentDependencies.stream()
                .filter(parent -> parent instanceof ProjectNode)
                .map(parent -> ((ProjectNode) parent).getDisplayName())
                .sorted()
                .collect(Collectors.toList());
    }
}
