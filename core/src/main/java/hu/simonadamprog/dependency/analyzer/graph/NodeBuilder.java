package hu.simonadamprog.dependency.analyzer.graph;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

class NodeBuilder {

    private Map<String, DependencyNode> map;
    private String combinedId;
    private String projectName;
    private DependencyNode createdNode;

    public static NodeBuilder create() {
        return new NodeBuilder();
    }

    private NodeBuilder() {}

    public NodeBuilder map(Map<String, DependencyNode> map) {
        this.map = map;
        return this;
    }

    public NodeBuilder combinedId(String combinedId) {
        this.combinedId = combinedId;
        return this;
    }

    public NodeBuilder projectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public void build() {
        createNodeBasedOnType();
        addToMap();
    }

    private void createNodeBasedOnType() {
        if (isProject()) {
            createProjectNode();
        }
        else {
            createLibraryNode();
        }
    }

    private boolean isProject() {
        return StringUtils.isNotBlank(projectName);
    }

    private void createProjectNode() {
        createdNode = ProjectNode.create(combinedId, projectName);
    }

    private void createLibraryNode() {
        createdNode = DependencyNode.create(combinedId);
    }

    private void addToMap() {
        map.put(combinedId, createdNode);
    }
}
