package org.eclipse.jetty.demo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.toolchain.test.FS;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;

public class ZipFsExampleCreator
{
    public static Path create(String filename) throws IOException
    {
        Path outputJar = MavenTestingUtils.getTargetTestingPath(ZipFsExampleCreator.class.getSimpleName()).resolve(filename).toAbsolutePath();

        FS.ensureDirExists(outputJar.getParent());

        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        URI uri = URI.create("jar:" + outputJar.toUri().toASCIIString());
        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env))
        {
            Path base = zipfs.getPath("/");

            touchFile(base.resolve("META-INF/MANIFEST.MF"), "Manifest-Version: 1.0\n" +
                "Multi-Release: true\n" +
                "Created-By: " + ZipFsExampleCreator.class.getName() + "\n");

            touchFile(base.resolve("exists.txt"), "Base exists");

            touchFile(base.resolve("/META-INF/versions/8/exists-in-8.txt"), "Versions 8 exists");
            touchFile(base.resolve("/META-INF/versions/9/exists-in-9.txt"), "Versions 9 exists");
            touchFile(base.resolve("/META-INF/versions/11/exists-in-11.txt"), "Versions 11 exists");
            touchFile(base.resolve("/META-INF/versions/17/exists-in-17.txt"), "Versions 17 exists");

            touchFile(base.resolve("/META-INF/versions/10/org/example/In10Only.class"), "10 specific class (only)");
            touchFile(base.resolve("/META-INF/versions/10/org/example/InBoth.class"), "10 specific class (both)");

            touchFile(base.resolve("/META-INF/versions/9/org/example/Nowhere$NoOuter.class"), "9 specific class (noouter)");
            touchFile(base.resolve("/META-INF/versions/9/org/example/InBoth$Inner9.class"), "9 specific class (both/inner9)");
            touchFile(base.resolve("/META-INF/versions/9/org/example/InBoth$InnerBoth.class"), "9 specific class (both/innerboth)");
            touchFile(base.resolve("/META-INF/versions/9/org/example/OnlyIn9.class"), "9 specific class (only)");
            touchFile(base.resolve("/META-INF/versions/9/org/example/InBoth.class"), "9 specific class (both)");
            touchFile(base.resolve("/META-INF/versions/9/org/example/onlyIn9/OnlyIn9.class"), "9 specific class (sub-package/only)");

            touchFile(base.resolve("/META-INF/versions/11/org/example/InMulti.class"), "11 specific class (multi)");
            touchFile(base.resolve("/META-INF/versions/11/WEB-INF/classes/App.class"), "11 specific class (webinf classes app)");
            touchFile(base.resolve("/META-INF/versions/11/WEB-INF/lib/depend11.jar"), "11 specific jar (webinf lib depend11)");
            touchFile(base.resolve("/META-INF/versions/11/WEB-INF/web.xml"), "11 specific xml (web.xml)");

            touchFile(base.resolve("/META-INF/versions/17/WEB-INF/lib/depend17.jar"), "17 specific jar (webinf lib depend17)");

            touchFile(base.resolve("/WEB-INF/web.xml"), "base class (web.xml)");
            touchFile(base.resolve("/WEB-INF/lib/base.jar"), "base jar (webinf lib base)");
            touchFile(base.resolve("/WEB-INF/classes/org/example/base/App.class"), "base class (webinf classes)");

            touchFile(base.resolve("/org/example/InBoth$InnerBoth.class"), "base class (both)");
            touchFile(base.resolve("/org/example/OnlyInBase.class"), "base class (only in base)");
            touchFile(base.resolve("/org/example/InBoth.class"), "base class (both)");
            touchFile(base.resolve("/org/example/InBoth$InnerBase.class"), "base class (both / inner)");
        }

        return outputJar;
    }

    private static void touchFile(Path file, String contents) throws IOException
    {
        Path dir = file.getParent();
        if (dir != null && !Files.exists(dir))
        {
            Files.createDirectories(dir);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8))
        {
            writer.write(contents);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
