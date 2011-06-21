package org.projectodd.stilts.clownshoes.stomplet;

import org.projectodd.stilts.StompException;

public class NoSuchStompletException extends StompException {

    private static final long serialVersionUID = 1L;
    
    private String className;

    public NoSuchStompletException(String className) {
        super( "No such stomplet class: " + className );
        this.className = className;
    }
    
    public String getClassName() {
        return this.className;
    }
}
