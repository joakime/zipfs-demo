package org.eclipse.jetty.demo;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class FindPredicate implements BiPredicate<Path, BasicFileAttributes>
{
    private final Predicate<Path> pathPredicate;

    public FindPredicate(Predicate<Path> pathPredicate)
    {
        this.pathPredicate = pathPredicate;
    }

    @Override
    public boolean test(Path path, BasicFileAttributes basicFileAttributes)
    {
        return pathPredicate.test(path);
    }
}
