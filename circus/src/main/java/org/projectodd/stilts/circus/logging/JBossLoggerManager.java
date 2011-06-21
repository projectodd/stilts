/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

package org.projectodd.stilts.circus.logging;

import org.projectodd.stilts.logging.Logger;
import org.projectodd.stilts.logging.LoggerManager;

public class JBossLoggerManager implements LoggerManager {

	@Override
	public Logger getLogger(String name) {
		return new JBossLogger( org.jboss.logging.Logger.getLogger( "org.projectodd.stilts." + name) );
	}

}
