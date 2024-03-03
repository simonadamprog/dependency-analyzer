package hu.simonadamprog.dependency.analyzer.graph;

final class ProjectNode extends DependencyNode {

    private final String displayName;

    private ProjectNode(String combinedId, String displayName) {
        super(combinedId);
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ProjectNode create(String combinedId, String displayName) {
        return new ProjectNode(combinedId, displayName);
    }
}
