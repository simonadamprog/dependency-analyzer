package hu.simonadamprog.dependency.analyzer.graph;

import hu.simonadamprog.dependency.analyzer.display.RootLibraryDetails;

import java.util.*;
import java.util.stream.Collectors;

public class DependencyGraph {

    private final Map<String, DependencyNode> dependencyMap;

    final Set<List<String>> circularityStore;

    private final Deque<String> dependencyStack;

    private int creationCounter = 0;

    private int connectionCounter = 0;

    public static DependencyGraph create() {
        return new DependencyGraph();
    }

    private DependencyGraph() {
        dependencyMap = new TreeMap<>();
        circularityStore = new HashSet<>();
        dependencyStack = new ArrayDeque<>();
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

    public List<RootLibraryDetails> getRootLibraryDetails(String libraryId) {
        if (notContainsDependency(libraryId)) {
            return Collections.emptyList();
        }
        DependencyNode node = dependencyMap.get(libraryId);
        Set<String> rootLibraries = getRootLibrariesRecursively(node);
        return gatherRootLibraryDetails(rootLibraries);
    }

    private Set<String> getRootLibrariesRecursively(DependencyNode node) {

        Set<String> rootLibraryIds;

        if (isProjectNodeOrIsCircular(node)) {
            return Collections.emptySet();
        }

        optInForCircularityCheck(node.getCombinedId());

        rootLibraryIds = gatherRootLibrariesOfParents(node);

        optOutFromCircularityCheck();

        return rootLibraryIds;
    }

    private boolean isProjectNodeOrIsCircular(DependencyNode node) {
        return isProjectNode(node) || isCircular(node.combinedId);
    }

    private boolean isCircular(String dependencyId) {
        return dependencyStack.contains(dependencyId);
    }

    private void optInForCircularityCheck(String dependencyId) {
        dependencyStack.push(dependencyId);
    }

    private Set<String> gatherRootLibrariesOfParents(DependencyNode node) {
        Set<String> rootLibraryIds = new HashSet<>();
        node.parentDependencies.forEach(parent -> {
            if (isProjectNode(parent)) {
                rootLibraryIds.add(node.getCombinedId());
            }
            rootLibraryIds.addAll(getRootLibrariesRecursively(parent));
        });
        return rootLibraryIds;
    }

    private void optOutFromCircularityCheck() {
        dependencyStack.pop();
    }

    private List<RootLibraryDetails> gatherRootLibraryDetails(Set<String> rootLibraries) {
        return rootLibraries.stream()
                .sorted()
                .map(this::mapRootLibraryIdToRootLibraryDetails)
                .collect(Collectors.toList());
    }

    private RootLibraryDetails mapRootLibraryIdToRootLibraryDetails(String libraryId) {
        List<String> dependingModules = getProjectParents(libraryId);
        return new RootLibraryDetails(libraryId, dependingModules);
    }

    public List<String> getProjectParents(String libraryId) {
        if (notContainsDependency(libraryId)) {
            return Collections.emptyList();
        }
        DependencyNode node = dependencyMap.get(libraryId);
        return gatherProjectParents(node);
    }

    private List<String> gatherProjectParents(DependencyNode node) {
        return node.parentDependencies.stream()
                .filter(this::isProjectNode)
                .map(this::getProjectDisplayName)
                .sorted()
                .collect(Collectors.toList());
    }

    private String getProjectDisplayName(DependencyNode node) {
        return ((ProjectNode) node).getDisplayName();
    }
}
