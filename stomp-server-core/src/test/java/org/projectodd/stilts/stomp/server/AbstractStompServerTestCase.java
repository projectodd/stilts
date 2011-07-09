package org.projectodd.stilts.stomp.server;

import org.junit.After;
import org.junit.Before;
import org.projectodd.stilts.stomp.spi.StompProvider;

public abstract class AbstractStompServerTestCase<T extends StompProvider> {
	
	protected StompServer<T> server;

	@Before
	public void setUpServer() throws Exception {
		this.server = createServer();
		this.server.start();
	}
	
	@After
	public void tearDownServer() throws Exception {
		this.server.stop();
		this.server = null;
		
	}
	
	public StompServer<T> getServer() {
		return this.server;
	}
	
	protected abstract StompServer<T> createServer() throws Exception;

}
