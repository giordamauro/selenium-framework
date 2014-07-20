package com.mgiorda.context.run;

import java.util.ArrayList;
import java.util.List;

import com.mgiorda.testng.run.MultipleSuiteRunner;
import com.mgiorda.testng.run.SuiteRunner;

public class ManyContextsSuiteRunner implements SuiteRunner<ContextsSuiteConfiguration> {

	private final SuiteRunner<SuiteConfiguration> suiteRunner;
	private final MultipleSuiteRunner<SuiteConfiguration> multipleSuiteRunner;

	private boolean runInParallel = false;

	public ManyContextsSuiteRunner(SuiteRunner<SuiteConfiguration> suiteRunner) {

		this.suiteRunner = suiteRunner;
		this.multipleSuiteRunner = new MultipleSuiteRunner<>(suiteRunner);
	}

	public boolean isRunInParallel() {
		return runInParallel;
	}

	public void setRunInParallel(boolean runInParallel) {
		this.runInParallel = runInParallel;
	}

	@Override
	public void runSuite(ContextsSuiteConfiguration suiteConfiguration) {

		if (suiteConfiguration == null) {
			throw new IllegalStateException("Suite config cannot be null");
		}

		List<SuiteConfiguration> suites = new ArrayList<>();

		String[] contextFiles = suiteConfiguration.getContexts();
		for (String context : contextFiles) {

			SuiteConfiguration suiteConfig = new SuiteConfiguration(suiteConfiguration.getFile());
			suiteConfig.setContext(context);
			suiteConfig.setProperties(suiteConfiguration.getProperties());
			suiteConfig.setNameSuffix(suiteConfiguration.getNameSuffix());
			suiteConfig.setOutputDirectory(suiteConfiguration.getOutputDirectory());

			suites.add(suiteConfig);
		}

		if (suites.size() == 1) {
			suiteRunner.runSuite(suites.get(0));
		} else {
			multipleSuiteRunner.run(suites, runInParallel);
		}
	}
}
