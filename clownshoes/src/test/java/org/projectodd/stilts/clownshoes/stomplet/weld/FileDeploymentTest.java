/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

package org.projectodd.stilts.clownshoes.stomplet.weld;

import java.io.File;

import org.junit.Test;
import org.junit.Ignore;
import org.projectodd.stilts.clownshoes.weld.FileBeanDeploymentArchive;

public class FileDeploymentTest {

    @Test
    @Ignore
    public void testScanDirectory() throws Exception {
        FileBeanDeploymentArchive deployment = new FileBeanDeploymentArchive( new File( "./target/stilts.jar" ) );
    }
}
