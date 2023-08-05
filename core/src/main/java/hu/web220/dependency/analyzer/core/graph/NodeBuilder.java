package hu.web220.dependency.analyzer.core.graph;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

class NodeBuilder {

    private Map<String, DependencyNode> map;
    private String combinedId;
    private String projectName;

    public static NodeBuilder create() {
        return new NodeBuilder();
    }

    private NodeBuilder() {

    }

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

    public DependencyNode build() {
        DependencyNode node;
        if (StringUtils.isNotBlank(projectName)) {
            node = ProjectNode.create(combinedId, projectName);
        }
        else {
            node = DependencyNode.create(combinedId);
        }
        map.put(combinedId, node);
        return node;
    }
}
