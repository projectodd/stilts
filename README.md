Stilts - STOMP Integration Layer 
================================

http://stilts.projectodd.org/

Building
--------

To build, you need Maven 3 and the appropriate entries in your
~/.m2/settings.xml to use the JBoss maven artifact repositories.

Alternatively, you may use the included support/settings.xml via

<pre>
    mvn -s ./support/settings.xml install
</pre>

JBossAS Integration
-------------------

Make sure you have a JBossAS version that supports the jboss netty bundle.
Check if this [pull request](http://github.com/jbossas/jboss-as/pull/257) already made it upstream.
If not, use [this branch](http://github.com/tdiesler/jboss-as/commits/as1743).

Add the the netty bundle to the list of auto installed bundles.

<pre>
    &lt;modules>
        ...
        &lt;module identifier="org.jboss.netty" startlevel="2"/>
        ...
    &lt;/modules>
</pre>

Set the JBOSS_HOME environment variable and copy the stilts-stomplet-server-bundle.jar to the deployments folder.

<pre>
    $ export JBOSS_HOME=~/git/.../build/target/jboss-as-7.1.0.Alpha1-SNAPSHOT
	$ mvn install
	$ cp stomplet-server-bundle/target/stilts-stomplet-server-bundle.jar $JBOSS_HOME/standalone/deployments/
</pre>

Startup the server. You should see

<pre>
	20:02:42,327 INFO  [org.jboss.osgi.framework.internal.BundleManager] (MSC service thread 1-3) Install bundle: stilts-stomplet-server-bundle:0.1.16.SNAPSHOT
	20:02:42,473 INFO  [org.jboss.as.server.controller] (DeploymentScanner-threads - 2) Deployed "stilts-stomplet-server-bundle.jar"
	20:02:42,527 INFO  [org.projectodd.stilts.stomplet.bundle.StompletServerActivator] (MSC service thread 1-4) start: BundleContext[stilts-stomplet-server-bundle:0.1.16.SNAPSHOT]
	20:02:42,546 INFO  [org.projectodd.stilts.stomplet.bundle.StompletServerActivator] (MSC service thread 1-4) adding transaction manager: com.arjuna.ats.jbossatx.jta.TransactionManagerDelegate@cbbaf
	20:02:42,628 INFO  [org.jboss.osgi.framework.internal.HostBundleState] (MSC service thread 1-4) Bundle started: stilts-stomplet-server-bund
</pre>

Now you can run the integration tests

<pre>
	$ mvn -Djbossas install
	...
	Running org.projectodd.stilts.stomplet.StompletServerTestCase
    Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 4.292 sec
</pre>

How does it work?
-----------------

The stilts-stomplet-server-bundle has a [BundleActivator](http://www.osgi.org/javadoc/r4v42/org/osgi/framework/BundleActivator.html) that 

* starts the [StompletServer](http://stilts.projectodd.org/javadocs/org/projectodd/stilts/stomplet/StompletServer.html)
* tracks [Stomplet](http://stilts.projectodd.org/javadocs/org/projectodd/stilts/stomplet/Stomplet.html) OSGi services

The test bundle has a BundleActivator that registers a Stomplet service

<pre>
    public void start(BundleContext context) throws Exception {
        log.infof("start: %s", context);
        Dictionary<String, String> props = new Hashtable<String, String>();
        props.put("destinationPattern", DESTINATION_QUEUE_ONE);
        registration = context.registerService(Stomplet.class.getName(), new SimpleTestStomplet(), props);
    }
</pre> 

When this service gets tracked, the stilts-stomplet-server-bundle adds it to the running container

<pre>
        public Object addingService(ServiceReference reference) {
            Stomplet stomplet = (Stomplet) super.addingService(reference);
            log.infof("adding: %s", stomplet);
            try {
                // Copy string properties
                Map<String, String> props = new HashMap<String, String>();
                for (String key : reference.getPropertyKeys()) {
                    Object value = reference.getProperty(key);
                    if (value instanceof String) {
                        props.put(key, (String) value);
                    }
                }
                String destinationPattern = props.get("destinationPattern");
                log.infof("adding: %s -> %s", destinationPattern, stomplet);
                container.addStomplet(destinationPattern, stomplet, props);
            } catch (StompException ex) {
                log.errorf(ex, "Cannot add stomplet: %s", stomplet);
            }
            return stomplet;
        }
</pre>

The test client code should be obvious.

<pre>
        StompClient client = new StompClient("stomp://localhost");
        client.connect();

        final CountDownLatch latch = new CountDownLatch(3);
        SubscriptionBuilder builder = client.subscribe(DESTINATION_QUEUE_ONE);
        builder.withMessageHandler(new MessageHandler() {
            public void handle(StompMessage message) {
                latch.countDown();
            }
        });
        ClientSubscription subscription = builder.start();
        
        client.send(StompMessages.createStompMessage(DESTINATION_QUEUE_ONE, "start"));

        assertTrue("No latch timeout", latch.await(10, TimeUnit.SECONDS));

        client.send(StompMessages.createStompMessage(DESTINATION_QUEUE_ONE, "stop"));

        subscription.unsubscribe();
        client.disconnect();
</pre>