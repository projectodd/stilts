package org.projectodd.stilts.stomplet;

import javax.transaction.TransactionManager;

import org.junit.After;
import org.junit.Before;

import com.arjuna.ats.jta.common.jtaPropertyManager;

public abstract class AbstractStompletServerTestCase {
	
	protected StompletServer server;

	@Before
	public void setUpServer() throws Exception {
		this.server = new StompletServer();
		this.server.setTransactionManager( getTransactionManager() );
		this.server.start();
		configureServer();
	}
	
	public abstract void configureServer() throws Exception;
	
	
    @After
	public void tearDownServer() throws Exception {
		this.server.stop();
		this.server = null;
	}
	
	public StompletServer getServer() {
		return this.server;
	}
	
	private TransactionManager getTransactionManager() {
        return jtaPropertyManager.getJTAEnvironmentBean().getTransactionManager();
    }

}
