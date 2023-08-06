/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package hu.web220.dependency.analyzer.core;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A simple functional test for the 'hu.web220.dependency.analyzer.core' plugin.
 */
class DependencyAnalyzerPluginFunctionalTest {
    File projectDir = new File("../");

    @Test void canRunTask() {
        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments("searchLibraryConnections", "-Plibrary=org.apache.commons:commons-math3:3.6.1");
        runner.withProjectDir(projectDir);
        BuildResult result = runner.build();

        // Verify the result
        assertTrue(result.getOutput().contains("Given input (trimmed to 200 character length) is: org.apache.commons:commons-math3:3.6.1"));
        assertTrue(result.getOutput().contains("!!! Warning !!! Circular dependencies detected"));
    }
}
