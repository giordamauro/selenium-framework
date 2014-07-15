package com.mgiorda.testng.run;

public interface SuiteRunner<E extends SuiteConfiguration> {

    void runSuite(E suite);
}
