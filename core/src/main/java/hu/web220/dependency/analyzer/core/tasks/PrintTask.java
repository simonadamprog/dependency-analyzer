package hu.web220.dependency.analyzer.core.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.*;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.artifacts.result.ArtifactResolutionResult;
import org.gradle.api.component.Component;
import org.gradle.api.tasks.TaskAction;
import org.gradle.jvm.JvmLibrary;
import org.gradle.language.base.artifact.SourcesArtifact;
import org.gradle.language.java.artifact.JavadocArtifact;
import org.gradle.maven.MavenPomArtifact;
import org.gradle.platform.base.Library;

import java.util.Set;

public abstract class PrintTask extends DefaultTask {

    @TaskAction
    public void perform() {
        System.out.println("Hello from plugin 'hu.web220.dependency.analyzer'");
        ConfigurationContainer configurations = getProject().getConfigurations();
        configurations.forEach(this::printConfiguration);
    }

    private void printConfiguration(Configuration configuration) {
        System.out.println("Configuration: " + configuration.getName());
        if (!configuration.isCanBeResolved()) {
            System.out.println(" Configuration cannot be resolved!");
            return;
        }
        Set<ResolvedDependency> level1 = configuration.getResolvedConfiguration().getFirstLevelModuleDependencies();
        level1.forEach(resolvedDependency -> printDependency(resolvedDependency, 1));
    }

    private void printDependency(ResolvedDependency dependency, int level) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            stringBuilder.append(" ");
        }
        stringBuilder.append(dependency.getName());
        System.out.println(stringBuilder);

        dependency.getChildren().forEach(child -> printDependency(child, level + 1));
    }
}
