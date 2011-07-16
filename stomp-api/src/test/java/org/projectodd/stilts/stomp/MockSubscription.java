package org.projectodd.stilts.stomp;


public class MockSubscription implements Subscription {

    public MockSubscription(String id, String destination, Headers headers) {
        this.id = id;
        this.destination = destination;
        this.headers = headers;
    }

    @Override
    public String getId() {
        return this.id;
    }
    
    public String getDestination() {
        return this.destination;
    }
    
    public Headers getHeaders() {
        return this.headers;
    }

    @Override
    public void cancel() throws StompException {
        
    }
    
    private String id;
    private String destination;
    private Headers headers;

}
