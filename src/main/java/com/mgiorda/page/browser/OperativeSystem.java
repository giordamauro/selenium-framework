package com.mgiorda.page.browser;

public enum OperativeSystem {

	WINDOWS, UNIX, MAC_OS;

	public static OperativeSystem getCurrentOS() {

		OperativeSystem operativeSystem = null;

		String os = System.getProperty("os.name").toLowerCase();

		if (os.indexOf("win") >= 0) {
			operativeSystem = WINDOWS;

		} else if (os.indexOf("mac") >= 0) {
			operativeSystem = MAC_OS;

		} else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0) {
			operativeSystem = UNIX;

		} else {
			throw new IllegalStateException(String.format("Couldn't get current OperativeSystem for os '%s'", os));
		}

		return operativeSystem;
	}
}
