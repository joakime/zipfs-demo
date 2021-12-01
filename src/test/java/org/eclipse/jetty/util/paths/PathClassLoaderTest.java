//
// ========================================================================
// Copyright (c) 1995-2021 Mort Bay Consulting Pty Ltd and others.
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Public License v. 2.0 which is available at
// https://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
// which is available at https://www.apache.org/licenses/LICENSE-2.0.
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
// ========================================================================
//

package org.eclipse.jetty.util.paths;

import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PathClassLoaderTest
{
    @Test
    public void testLoadClass() throws Exception
    {
        try (PathLoader pathLoader = new PathLoader())
        {
            pathLoader.add(MavenTestingUtils.getTestResourcePathFile("example.jar"));
            PathClassLoader classLoader = new PathClassLoader(pathLoader);
            Class<?> clazz = classLoader.loadClass("org.example.InBoth");
            assertNotNull(clazz);
        }
    }
}
