package org.projectodd.stilts.clownshoes.stomplet.weld;

import java.io.File;

import org.junit.Test;
import org.projectodd.stilts.clownshoes.weld.FileBeanDeploymentArchive;

public class FileDeploymentTest {

    @Test
    public void testScanDirectory() throws Exception {
        FileBeanDeploymentArchive deployment = new FileBeanDeploymentArchive( new File( "./target/stilts.jar" ) );
    }
}
