package com.github.ncredinburgh.tomcat;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Properties;

import org.junit.Test;

import com.github.ncredinburgh.tomcat.PropertyParser;

public class TestPropertyParser {

	@Test
	public void shouldParseOneProperty() throws Exception {
		String propertyString = "prop1=value1";
		
		Properties result = PropertyParser.parseProperties(propertyString);
		
		assertThat(result.size(), equalTo(1));
		assertThat(result.containsKey("prop1"), equalTo(true));
		assertThat(result.getProperty("prop1"), equalTo("value1"));
	}
	
	@Test
	public void shouldParseTwoProperties() throws Exception {
		String propertyString = "prop1=value1;prop2=value2";
		
		Properties result = PropertyParser.parseProperties(propertyString);
		
		assertThat(result.size(), equalTo(2));
		assertThat(result.containsKey("prop1"), equalTo(true));
		assertThat(result.getProperty("prop1"), equalTo("value1"));
		assertThat(result.containsKey("prop2"), equalTo(true));
		assertThat(result.getProperty("prop2"), equalTo("value2"));
	}

	@Test
	public void shouldIgnoreTrailingSemicolon() throws Exception {
		String propertyString = "prop1=value1;";
		
		Properties result = PropertyParser.parseProperties(propertyString);
		
		assertThat(result.size(), equalTo(1));
		assertThat(result.containsKey("prop1"), equalTo(true));
		assertThat(result.getProperty("prop1"), equalTo("value1"));
	}

}
