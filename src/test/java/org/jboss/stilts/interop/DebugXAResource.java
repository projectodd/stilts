package org.jboss.stilts.interop;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class DebugXAResource implements XAResource {
    
    private String name;

    public DebugXAResource(String name) {
        this.name = name;
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        System.err.println( name + " --- commit(" + xid + ", " + onePhase + ")" );
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
        System.err.println( name + "end(" + xid + ", " + flags + ")" );
    }

    @Override
    public void forget(Xid xid) throws XAException {
        System.err.println( name + "forget(" + xid + ")" );
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        System.err.println( name + "getTransactionTimeout()" );
        return 0;
    }

    @Override
    public boolean isSameRM(XAResource xares) throws XAException {
        System.err.println( name + "isSameRM(" + xares + ")" );
        return false;
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        System.err.println( name + "prepare(" + xid + ")" );
        return 0;
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        System.err.println( name + "recover(" + flag + ")" );
        return null;
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        System.err.println( name + "rollback(" + xid + ")" );
    }

    @Override
    public boolean setTransactionTimeout(int seconds) throws XAException {
        System.err.println( name + "setTransactionTimeout(" + seconds + ")" );
        return false;
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {
        System.err.println( name + "start(" + xid + "," + flags + ")" );
    }

}
