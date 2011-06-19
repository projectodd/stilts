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

package org.projectodd.stilts.spi;

import org.projectodd.stilts.StompException;

public interface Subscription {
    
    public static enum AckMode {
        AUTO("auto"),
        CLIENT("client"),
        CLIENT_INDIVIDUAL("client-individual"),;
        
        private String str;

        AckMode(String str) {
            this.str = str;
        }
        
        public String toString() {
            return this.str;
        }
    }
    
    String getId();
    void cancel() throws StompException;
}
