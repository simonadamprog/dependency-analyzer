package hu.web220.dependency.analyzer.core.tasks;

import hu.web220.dependency.analyzer.core.DependencyAnalyzerPlugin;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.*;
import org.gradle.api.tasks.TaskAction;

import java.util.Set;

public abstract class DependencyTreePrintingTask extends DefaultTask {

    public DependencyTreePrintingTask() {
        setGroup(DependencyAnalyzerPlugin.TASK_GROUP);
    }

    @TaskAction
    public void perform() {
        System.out.println("Hello from plugin 'hu.web220.dependency.analyzer'");
        iterateConfigurations(getProject().getRootProject());
        getProject().getRootProject().getSubprojects().forEach(this::iterateConfigurations);
    }

    private void iterateConfigurations(Project project) {
        System.out.printf("--- Project is: %s:%s:%s. DisplayName: %s%n",
                project.getGroup(),
                project.getName(),
                project.getVersion(),
                project.getDisplayName()
                );
        ConfigurationContainer configurations = project.getConfigurations();
        configurations.forEach(this::printConfiguration);
    }

    private void printConfiguration(Configuration configuration) {
        System.out.println("Configuration: " + configuration.getName());
        if (!configuration.isCanBeResolved()) {
            System.out.println(" Configuration cannot be resolved!");
            return;
        }
        Set<ResolvedDependency> level1 = configuration.getResolvedConfiguration().getFirstLevelModuleDependencies();
        level1.forEach(resolvedDependency -> printDependency(resolvedDependency, 1 ));
    }

    private void printDependency(ResolvedDependency dependency, int level) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            stringBuilder.append("    ");
        }

        stringBuilder.append(dependency.getName());
        System.out.println(stringBuilder);

        dependency.getChildren().forEach(child -> printDependency(child, level + 1));
    }
}
