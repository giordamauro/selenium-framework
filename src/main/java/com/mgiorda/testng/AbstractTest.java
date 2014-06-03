package com.mgiorda.testng;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@ContextConfiguration(locations = { "classpath:/testsContext.xml" })
public class AbstractTest extends AbstractTestNGSpringContextTests {

}
