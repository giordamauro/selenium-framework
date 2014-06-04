package com.mgiorda.commons;

import java.io.IOException;

import org.apache.log4j.Layout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.spi.LoggingEvent;

public class ThreadIdRollingFileAppender extends RollingFileAppender {

	public ThreadIdRollingFileAppender() {

	}

	public ThreadIdRollingFileAppender(Layout layout, String filename) throws IOException {
		super(layout, filename);
	}

	public ThreadIdRollingFileAppender(Layout layout, String filename, boolean append) throws IOException {
		super(layout, filename, append);
	}

	@Override
	protected void subAppend(LoggingEvent event) {

		long threadId = Thread.currentThread().getId();

		String modifiedMessage = String.format("[thread: %s] - %s", threadId, event.getMessage());
		LoggingEvent modifiedEvent = new LoggingEvent(event.getFQNOfLoggerClass(), event.getLogger(), event.getTimeStamp(), event.getLevel(), modifiedMessage, event.getThreadName(),
				event.getThrowableInformation(), event.getNDC(), event.getLocationInformation(), event.getProperties());

		super.subAppend(modifiedEvent);
	}
}
