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

public class JBossLogger implements Logger {

	public JBossLogger(org.jboss.logging.Logger jbossLogger) {
		this.jbossLogger = jbossLogger;
	}
	
	@Override
	public void fatal(Object message) {
		this.jbossLogger.fatal( message );
	}

	@Override
	public void fatal(Object message, Throwable t) {
		this.jbossLogger.fatal( message, t);
	}

	@Override
	public void error(Object message) {
		this.jbossLogger.error(message);
	}

	@Override
	public void error(Object message, Throwable t) {
		this.jbossLogger.error(message, t);
	}

	@Override
	public void warn(Object message) {
		this.jbossLogger.warn(message);
	}

	@Override
	public void info(Object message) {
		this.jbossLogger.info(message);
	}

	@Override
	public void debug(Object message) {
		this.jbossLogger.debug(message);
	}

	@Override
	public void trace(Object message) {
		this.jbossLogger.trace(message);
	}

	private org.jboss.logging.Logger jbossLogger;

}
