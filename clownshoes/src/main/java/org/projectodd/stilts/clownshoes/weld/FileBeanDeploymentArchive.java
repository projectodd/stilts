/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.projectodd.stilts.clownshoes.weld;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileBeanDeploymentArchive extends SimpleCircusBeanDeploymentArchive {

    public FileBeanDeploymentArchive(File file) throws Exception {
        this( file, null );
    }

    public FileBeanDeploymentArchive(File file, ClassLoader parent) throws Exception {
        super( file.getAbsolutePath(), new URLClassLoader( new URL[] { file.toURI().toURL() }, parent ) );
        this.file = file;
        scanDeployment();
    }

    protected void scanDeployment() throws IOException {
        if (this.file.isDirectory()) {
            scanDirectory();
        } else {
            scanArchive();
        }
    }

    protected void scanDirectory() {
        scanDirectory( null, this.file );
    }

    protected void scanDirectory(String prefix, File dir) {
        if (prefix == null) {
            prefix = "";
        } else {
            prefix = prefix + ".";
        }

        for (File child : dir.listFiles()) {
            if (child.isDirectory()) {
                scanDirectory( prefix + child.getName(), child );
            } else {
                String name = child.getName();
                if (name.endsWith( ".class" )) {
                    name = name.substring( 0, name.length() - 6 );
                    addBeanClass( prefix + name );
                }
            }
        }
    }

    protected void scanArchive() throws IOException {
        JarFile archive = new JarFile( this.file );

        Enumeration<JarEntry> entries = archive.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.endsWith( ".class" )) {
                name = name.substring( 0, name.length() - 6 );
                name = name.replaceAll( "/", "." );
                addBeanClass( name );
            }
        }
    }

    private File file;

}
