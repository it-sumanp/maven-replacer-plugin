package com.google.code.maven_replacer_plugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;


public class DelimiterTest {
	private static final String VALUE_WITHOUT_MIDDLE = "abc";
	private static final String VALUE_WITH_MIDDLE_START = "123";
	private static final String VALUE_WITH_MIDDLE_END = "456";
	private static final String TOKEN = "token";

	@Test
	public void shouldReturnUnchangedTokenWhenNoValueGiven() {
		assertThat(new Delimiter(null).apply(TOKEN), equalTo(TOKEN));
		assertThat(new Delimiter("").apply(TOKEN), equalTo(TOKEN));
	}
	
	@Test
	public void shouldReturnTokenWithValueAtStartAndEndWhenNoMiddle() {
		String result = new Delimiter(VALUE_WITHOUT_MIDDLE).apply(TOKEN);
		assertThat(result, equalTo(VALUE_WITHOUT_MIDDLE + TOKEN + VALUE_WITHOUT_MIDDLE));
	}
	
	@Test
	public void shouldReturnTokenWithSplitValueAtStartAndEndWhenHasMiddleAsterix() {
		String result = new Delimiter(VALUE_WITH_MIDDLE_START + "*" + VALUE_WITH_MIDDLE_END).apply(TOKEN);
		assertThat(result, equalTo(VALUE_WITH_MIDDLE_START + TOKEN + VALUE_WITH_MIDDLE_END));
	}
}
