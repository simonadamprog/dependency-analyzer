package hu.web220.dependency.analyzer.core.graph;

import java.util.HashSet;

class DependencyNode {

    // "{group}:{name}:{version}"
    protected final String combinedId;

    protected final HashSet<DependencyNode> parentDependencies;

    protected final HashSet<DependencyNode> childDependencies;

    protected DependencyNode(String combinedId) {
        this.combinedId = combinedId;
        this.parentDependencies = new HashSet<>();
        this.childDependencies = new HashSet<>();
    }

    public boolean containsParentDependency(DependencyNode parentDependencyCandidate) {
        return this.parentDependencies.contains(parentDependencyCandidate);
    }

    public boolean containsChildDependency(DependencyNode childDependencyCandidate) {
        return this.childDependencies.contains(childDependencyCandidate);
    }

    public void addChildDependency(DependencyNode childDependency) {
        this.childDependencies.add(childDependency);
    }

    public void addParentDependency(DependencyNode parentDependency) {
        this.parentDependencies.add(parentDependency);
    }

    public static DependencyNode create(String combinedId) {
        return new DependencyNode(combinedId);
    }
}
