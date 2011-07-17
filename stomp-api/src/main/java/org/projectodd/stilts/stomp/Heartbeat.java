package org.projectodd.stilts.stomp;

public class Heartbeat {

    public int getClientReceive() {
        return clientReceive;
    }

    public int getClientSend() {
        return clientSend;
    }
    
    public long getLastUpdate() {
        return lastUpdate;
    }

    public int getServerReceive() {
        return serverReceive;
    }

    public int getServerSend() {
        return serverSend;
    }

    public void setClientReceive(int clientReceive) {
        this.clientReceive = clientReceive;
    }

    public void setClientSend(int clientSend) {
        this.clientSend = clientSend;
    }

    public void setServerReceive(int serverReceive) {
        this.serverReceive = serverReceive;
    }

    public void setServerSend(int serverSend) {
        this.serverSend = serverSend;
    }

    public synchronized void touch() {
        lastUpdate = System.currentTimeMillis();
    }

    private int clientSend;
    private int clientReceive;
    private int serverSend = 60000;
    private int serverReceive = 60000;
    private long lastUpdate = System.currentTimeMillis();

}
