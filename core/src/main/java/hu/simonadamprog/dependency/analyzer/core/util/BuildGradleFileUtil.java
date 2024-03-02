package hu.simonadamprog.dependency.analyzer.core.util;

import org.gradle.api.Project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

public class BuildGradleFileUtil {

    private BuildGradleFileUtil() {
        // Util class.
    }

    public static boolean isAnyBuildFileModifiedSince(Project rootProject, Instant timestamp) {
        if (isFresh(rootProject, timestamp)) {
            return true;
        }
        return rootProject.getSubprojects().stream()
                .anyMatch(project -> isFresh(project, timestamp));
    }

    private static boolean isFresh(Project project, Instant timestamp) {
        try {
            Path path = project.getBuildFile().toPath();
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            return attributes.lastModifiedTime().toInstant().isAfter(timestamp);
        }
        catch (IOException e) {
            return true;
        }
    }
}
