package org.projectodd.stilts.conduit;

import javax.transaction.TransactionManager;

import org.junit.After;
import org.junit.Before;
import org.projectodd.stilts.conduit.spi.NontransactionalMessageConduitFactory;

import com.arjuna.ats.jta.common.jtaPropertyManager;

public abstract class AbstractConduitServerTestCase<T extends NontransactionalMessageConduitFactory> {
	
	protected ConduitServer<T> server;

	@Before
	public void setUpServer() throws Exception {
		this.server = createServer();
		this.server.setTransactionManager( getTransactionManager() );
		this.server.start();
	}
	
	protected abstract ConduitServer<T> createServer() throws Exception;
	
    @After
	public void tearDownServer() throws Exception {
		this.server.stop();
		this.server = null;
	}
	
	public ConduitServer<T> getServer() {
		return this.server;
	}
	
	private TransactionManager getTransactionManager() {
        return jtaPropertyManager.getJTAEnvironmentBean().getTransactionManager();
    }

}
