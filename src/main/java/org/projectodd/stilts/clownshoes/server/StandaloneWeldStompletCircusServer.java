package org.projectodd.stilts.clownshoes.server;

import java.util.ArrayList;
import java.util.List;

import org.projectodd.stilts.circus.MessageConduitFactory;
import org.projectodd.stilts.circus.xa.XAMessageConduitFactory;
import org.projectodd.stilts.circus.xa.psuedo.PsuedoXAMessageConduitFactory;
import org.projectodd.stilts.clownshoes.stomplet.SimpleStompletContainer;
import org.projectodd.stilts.clownshoes.stomplet.StompletMessageConduitFactory;
import org.projectodd.stilts.clownshoes.weld.CircusBeanDeploymentArchive;
import org.projectodd.stilts.clownshoes.weld.WeldStompletContainer;

public class StandaloneWeldStompletCircusServer extends StandaloneStompletCircusServer {

    public StandaloneWeldStompletCircusServer(StompletCircusServer server) {
        super( server );
    }
    
    public WeldStompletContainer getStompletContainer() {
        return (WeldStompletContainer) super.getStompletContainer();
    }
    
    public void addBeanDeploymentArchive(CircusBeanDeploymentArchive archive) {
        this.archives.add( archive );
    }
    
    public List<CircusBeanDeploymentArchive> getBeanDeploymentArchives() {
        return this.archives;
    }
    
    public void configure() throws Throwable {
        super.configure();
        WeldStompletContainer stompletContainer = new WeldStompletContainer( true );
        MessageConduitFactory conduitFactory = new StompletMessageConduitFactory( stompletContainer );
        XAMessageConduitFactory xaConduitFactory = new PsuedoXAMessageConduitFactory( conduitFactory );
        getServer().setStompletContainer( stompletContainer );
        getServer().setMessageConduitFactory( xaConduitFactory );
        for ( CircusBeanDeploymentArchive each : archives ) {
            stompletContainer.addBeanDeploymentArchive( each );
        }
    }
    
    public void start() throws Throwable {
        super.start();
    }
    
    public void stop() throws Throwable {
        super.stop();
    }


    private List<CircusBeanDeploymentArchive> archives = new ArrayList<CircusBeanDeploymentArchive>();
}
