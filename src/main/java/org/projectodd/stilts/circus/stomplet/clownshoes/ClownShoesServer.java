package org.projectodd.stilts.circus.stomplet.clownshoes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.circus.stomplet.clownshoes.conf.RouteConfiguration;
import org.projectodd.stilts.circus.stomplet.clownshoes.conf.StompletConfParser;
import org.projectodd.stilts.circus.stomplet.server.StompletCircusServer;
import org.projectodd.stilts.circus.stomplet.weld.CircusBeanDeploymentArchive;
import org.projectodd.stilts.circus.stomplet.weld.FileBeanDeploymentArchive;
import org.projectodd.stilts.circus.stomplet.weld.server.StandaloneWeldStompletCircusServer;

public class ClownShoesServer extends StandaloneWeldStompletCircusServer {

    public ClownShoesServer(StompletCircusServer server) {
        super( server );
    }

    public void setDeploymentLocation(String deploymentLocation) {
        this.deploymentsDirectory = deploymentLocation;
    }

    public void start() throws Throwable {
        super.start();
        deployStompletConfs();
    }

    public void configure() throws Throwable {
        deployResourceAdaptors();
        deployArchives();
        super.configure();
    }

    public void stop() throws Throwable {
        super.stop();
    }

    protected void deployResourceAdaptors() throws Throwable {

        System.err.println( "Deploying RAs from: " + this.deploymentsDirectory );
        File dir = new File( this.deploymentsDirectory );

        if (dir.isDirectory()) {
            for (File each : dir.listFiles()) {
                if (each.isFile() && each.getName().endsWith( ".rar" )) {
                    deployResourceAdapter( each.toURI().toURL() );
                }
            }
        }
    }

    protected void deployArchives() throws Throwable {
        File dir = new File( this.deploymentsDirectory );
        for (File child : dir.listFiles()) {
            if (child.isFile() && child.getName().endsWith( ".jar" )) {
                CircusBeanDeploymentArchive archive = new FileBeanDeploymentArchive( child );
                addBeanDeploymentArchive( archive );
            }
        }
    }

    protected void deployStompletConfs() throws Throwable {
        File dir = new File( this.deploymentsDirectory );
        for (File child : dir.listFiles()) {
            if (child.isFile() && child.getName().endsWith( "stomplet.conf" )) {
                deployStompletConf( child );
            }
        }

    }

    protected void deployStompletConf(File stompConf) throws IOException, StompException {
        FileInputStream in = new FileInputStream( stompConf );
        try {
            StompletConfParser parser = new StompletConfParser( in );
            List<RouteConfiguration> configs = parser.parse();
            for ( RouteConfiguration each : configs ) {
                getStompletContainer().addStomplet( each.getPattern(), each.getStompletClassName() );
            }
        } finally {
            in.close();
        }
    }

    public void run() throws Throwable {
        final FutureTask<Void> stopFuture = new FutureTask<Void>( new Callable<Void>() {
            public Void call() throws Exception {
                try {
                    stop();
                } catch (Throwable e) {
                    throw new Exception( e );
                }
                return null;
            }
        } );
        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run() {
                System.err.println( "STOPPING: " );
                stopFuture.run();
            }
        } );
        start();
        stopFuture.get();
    }

    private String deploymentsDirectory;

    public static void main(String[] args) throws Throwable {
        if (args.length != 1) {
            throw new Exception( "Usage: clownshoes <deployments-dir>" );
        }

        String deploymentLocation = args[0];
        StompletCircusServer server = new StompletCircusServer();
        ClownShoesServer standalone = new ClownShoesServer( server );
        standalone.setDeploymentLocation( deploymentLocation );
        standalone.run();
    }

}
