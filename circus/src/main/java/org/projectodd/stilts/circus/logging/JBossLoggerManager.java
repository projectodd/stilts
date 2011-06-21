package org.projectodd.stilts.circus.logging;

import org.projectodd.stilts.logging.Logger;
import org.projectodd.stilts.logging.LoggerManager;

public class JBossLoggerManager implements LoggerManager {

	@Override
	public Logger getLogger(String name) {
		return new JBossLogger( org.jboss.logging.Logger.getLogger( "org.projectodd.stilts." + name) );
	}

}
