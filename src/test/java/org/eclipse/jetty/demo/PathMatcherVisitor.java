package org.eclipse.jetty.demo;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;

public class PathMatcherVisitor implements FileVisitor<Path>
{
    private final PathMatcher dirMatcher;
    private final PathMatcher fileMatcher;

    public PathMatcherVisitor(PathMatcher dirMatcher, PathMatcher fileMatcher)
    {
        this.dirMatcher = dirMatcher;
        this.fileMatcher = fileMatcher;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
    {
        if (dirMatcher.matches(dir))
            return FileVisitResult.CONTINUE;
        else
            return FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
    {
        if (fileMatcher.matches(file))
            System.out.printf("  [visit] %s%n", file);

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc)
    {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
    {
        return FileVisitResult.CONTINUE;
    }
}
