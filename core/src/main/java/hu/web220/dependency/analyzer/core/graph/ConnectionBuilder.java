package hu.web220.dependency.analyzer.core.graph;

class ConnectionBuilder {

    private DependencyGraph dependencyGraph;
    private String parentCombinedId;
    private String childCombinedId;
    private DependencyNode parent;
    private DependencyNode child;

    private ConnectionBuilder() {}

    public ConnectionBuilder dependencyGraph(DependencyGraph dependencyGraph) {
        this.dependencyGraph = dependencyGraph;
        return this;
    }

    public ConnectionBuilder parentCombinedId(String parentCombinedId) {
        this.parentCombinedId = parentCombinedId;
        return this;
    }

    public ConnectionBuilder childCombinedId(String childCombinedId) {
        this.childCombinedId = childCombinedId;
        return this;
    }

    public void build() {
        getNodes();
        addConnectionIfNotExists();
    }

    private void getNodes() {
        getParentNode();
        getChildNode();
    }

    private void getParentNode() {
        parent = dependencyGraph.getDependencyMap().get(parentCombinedId);
    }

    private void getChildNode() {
        child = dependencyGraph.getDependencyMap().get(childCombinedId);
    }

    private void addConnectionIfNotExists() {
        if (isNoConnection()) {
            addTwoWayConnection();
        }
    }

    private boolean isNoConnection() {
        return !child.containsParentDependency(parent);
    }

    private void addTwoWayConnection() {
        addChildConnectionToParent();
        addParentConnectionToChild();
        increaseConnectionCounter();
    }

    private void addChildConnectionToParent() {
        parent.addChildDependency(child);
    }

    private void addParentConnectionToChild() {
        child.addParentDependency(parent);
    }

    private void increaseConnectionCounter() {
        dependencyGraph.increaseConnectionCounter();
    }

    public static ConnectionBuilder create() {
        return new ConnectionBuilder();
    }
}