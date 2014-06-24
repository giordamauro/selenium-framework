package com.mgiorda.common;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

public class ThreadIdConsoleAppender extends ConsoleAppender {

	public ThreadIdConsoleAppender() {
	}

	public ThreadIdConsoleAppender(Layout layout) {
		super(layout);
	}

	public ThreadIdConsoleAppender(Layout layout, String target) {
		super(layout, target);
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
