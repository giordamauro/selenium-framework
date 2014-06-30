package com.mgiorda.context;

import org.springframework.context.support.GenericXmlApplicationContext;

public final class MainContextLoader {

	public static final String DEFAULT_CONTEXT = "application-context.xml";

	private MainContextLoader() {

	}

	@SuppressWarnings("resource")
	public static void main(String[] args) {

		String mainContext = DEFAULT_CONTEXT;

		if (args != null && args.length == 1) {
			mainContext = args[0];
		}

		new GenericXmlApplicationContext(mainContext);

	}
}
