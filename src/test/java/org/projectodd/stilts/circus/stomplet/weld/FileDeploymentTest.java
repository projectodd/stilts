package org.projectodd.stilts.circus.stomplet.weld;

import java.io.File;

import org.junit.Test;

public class FileDeploymentTest {

    @Test
    public void testScanDirectory() throws Exception {
        FileBeanDeploymentArchive deployment = new FileBeanDeploymentArchive( new File( "./target/stilts.jar" ) );
    }
}
