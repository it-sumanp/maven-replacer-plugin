package com.google.code.maven_replacer_plugin;



public interface Replacer {

	String replaceRegex(String content, String token, String value,int regexFlags);

	String replaceNonRegex(String content, String token, String value);
	
}
