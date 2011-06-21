/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.projectodd.stilts.clownshoes.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.clownshoes.parser.RouteConfiguration;
import org.projectodd.stilts.clownshoes.parser.StompletConfParser;
import org.projectodd.stilts.clownshoes.weld.CircusBeanDeploymentArchive;
import org.projectodd.stilts.clownshoes.weld.FileBeanDeploymentArchive;

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
                getStompletContainer().addStomplet( each.getPattern(), each.getStompletClassName(), each.getProperties() );
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
