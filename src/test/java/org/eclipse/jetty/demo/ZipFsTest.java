package org.eclipse.jetty.demo;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.eclipse.jetty.util.paths.RegexPathPredicate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ZipFsTest
{
    private static Path createdJar;

    @BeforeAll
    public static void setupJar() throws IOException
    {
        createdJar = ZipFsExampleCreator.create("mr-created.jar");
    }

    /**
     * Show relationship between Path and FileSystem.
     */
    @Test
    public void dumpFileStore() throws IOException
    {
        Map<String, String> env = new HashMap<>();
        env.put("multi-release", "runtime");

        dumpFileStore(createdJar);

        URI zipUri = URI.create("jar:" + createdJar.toUri().toASCIIString());

        try (FileSystem fs = FileSystems.newFileSystem(zipUri, env))
        {
            Path root = fs.getPath("/");
            dumpFileStore(root);
        }
    }

    /**
     * Show that you can get the full URI from the Path object within a zipfs
     */
    @Test
    public void testGetURIFromFileSystem() throws IOException
    {
        Map<String, String> env = new HashMap<>();
        env.put("multi-release", "runtime");

        URI zipUri = URI.create("jar:" + createdJar.toUri().toASCIIString());

        try (FileSystem fs = FileSystems.newFileSystem(zipUri, env))
        {
            Path inboth = fs.getPath("/org/example/InBoth.class");
            System.out.printf("URI for %s is %s%n", inboth, inboth.toUri());
        }
    }

    /**
     * Show using {@link Files#walk(Path, int, FileVisitOption...)} to stream the entries of the ZipFs
     */
    @Test
    public void testFilesWalk_ZipFsRoot() throws IOException
    {
        Map<String, String> env = new HashMap<>();
        env.put("multi-release", "runtime");

        URI zipUri = URI.create("jar:" + createdJar.toUri().toASCIIString());

        try (FileSystem fs = FileSystems.newFileSystem(zipUri, env))
        {
            Path root = fs.getPath("/");

            System.out.println("-- using straight Files.walk() --");
            // Show contents
            Files.walk(root)
                .filter(Files::isRegularFile)
                .filter(new RegexPathPredicate("^.*/(web|meta)-inf/.*$").negate())
                .sorted()
                .forEach((path) ->
                    System.out.printf("  %s%n", path));
        }
    }

    /**
     * Show using {@link Files#walkFileTree(Path, FileVisitor)} to visit the entries of the ZipFs
     */
    @Test
    public void testFilesWalkFileTree_ZipFsRoot() throws IOException
    {
        Map<String, String> env = new HashMap<>();
        env.put("multi-release", "runtime");

        URI zipUri = URI.create("jar:" + createdJar.toUri().toASCIIString());

        try (FileSystem fs = FileSystems.newFileSystem(zipUri, env))
        {
            Path root = fs.getPath("/");

            PathMatcher fileMatcher = fs.getPathMatcher("glob:**/*.class");
            PathMatcher dirMatcher = fs.getPathMatcher("glob:/{WEB,META}-INF/**");

            System.out.println();
            System.out.println("-- using Files.walkFileTree() with FileSystem specific FileVisitor and PathMatcher --");
            Files.walkFileTree(root, new PathMatcherVisitor(not(dirMatcher), fileMatcher));
        }
    }

    /**
     * Show using {@link Files#find(Path, int, BiPredicate, FileVisitOption...)} to stream the entries of the ZipFs
     */
    @Test
    public void testFilesFind_ZipFsRoot() throws IOException
    {
        Map<String, String> env = new HashMap<>();
        env.put("multi-release", "runtime");

        URI zipUri = URI.create("jar:" + createdJar.toUri().toASCIIString());

        try (FileSystem fs = FileSystems.newFileSystem(zipUri, env))
        {
            Path root = fs.getPath("/");

            System.out.println("-- using Files.find() with FileSystem specific FileVisitor and PathMatcher --");
            Files.find(root, 50, new FindPredicate(
                    Predicate.not(new RegexPathPredicate("^.*/(web|meta)-inf/.*$"))
                        .and(new RegexPathPredicate("^.*\\.class$"))
                ))
                .forEach((path) ->
                    System.out.printf("  [find] %s%n", path));
        }
    }

    private static void dumpFileStore(Path path)
    {
        try
        {
            FileStore fileStore = path.getFileSystem().provider().getFileStore(path);
            System.out.printf("FileStore for %s%n", path);
            System.out.printf("  - class() = %s%n", fileStore.getClass().getName());
            System.out.printf("  - toString() = %s%n", fileStore);
            System.out.printf("  - type() = %s%n", fileStore.type());
            System.out.printf("  - name() = %s%n", fileStore.name());
            System.out.printf("  - getTotalSpace() = %,d%n", fileStore.getTotalSpace());
//            System.out.printf("  - getUnallocatedSpace() = %,d%n", fileStore.getUnallocatedSpace());
//            System.out.printf("  - getUsableSpace() = %,d%n", fileStore.getUsableSpace());
            System.out.printf("  - isReadOnly() = %b%n", fileStore.isReadOnly());
            System.out.println();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static PathMatcher not(PathMatcher matcher)
    {
        return path -> !matcher.matches(path);
    }
}
