package org.jboss.stilts.interop;

import javax.transaction.TransactionManager;

import org.junit.Test;

import com.arjuna.ats.jta.common.jtaPropertyManager;

public class TransactionManagerTest {


    @Test
    public void testSettingUpTransactionManager() throws Exception {
        TransactionManager tm = jtaPropertyManager.getJTAEnvironmentBean().getTransactionManager();
        

        tm.begin();
        tm.getTransaction().enlistResource( new DebugXAResource( "0000000.") );
        tm.getTransaction().enlistResource( new DebugXAResource( "1111111.") );
        System.err.println( tm.getTransaction() );

        tm.commit();
        System.err.println( tm.getTransaction() );
    }

}
