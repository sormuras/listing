package com.github.sormuras.listing;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.runner.SelectPackages;
import org.junit.runner.RunWith;

/**
 * JUnit4Suite will discover and run all JUnit Jupiter tests in the {@code
 * com.github.sormuras.listing} package and its subpackages.
 *
 * @see <a href="http://junit.org/junit5/docs/snapshot/user-guide/#test-suite">Test Suite</a>
 */
@RunWith(JUnitPlatform.class)
@SelectPackages("com.github.sormuras.listing")
public class JUnit4Suite {}
