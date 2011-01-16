package com.google.code.maven_replacer_plugin;


public class Delimiter {
	private static final String FORMAT = "%s%s%s";
	
	private String delimiter;
	private String start;
	private String end;
	
	public Delimiter() {
		setDelimiter(null);
	}
	
	public Delimiter(String delimiter) {
		setDelimiter(delimiter);
	}

	private void buildStartAndEnd() {
		StringBuilder startBuilder = new StringBuilder();
		StringBuilder endBuilder = new StringBuilder();
		boolean buildingStart = true;
		boolean hasMiddle = false;
		for (char c : this.getDelimiter().toCharArray()) {
			if (c == '*') {
				buildingStart = false;
				hasMiddle = true;
				continue;
			}
			
			if (buildingStart) {
				startBuilder.append(c);
			} else {
				endBuilder.append(c);
			}
		}
		this.start = startBuilder.toString();
		if (hasMiddle) { 
			this.end = endBuilder.toString();
		} else {
			this.end = this.start;
		}
	}

	public String apply(String token) {
		if (token == null || token.isEmpty()) {
			return token;
		}

		buildStartAndEnd();
		return String.format(FORMAT, getStart(), token, getEnd());
	}
	
	private String getStart() {
		return start;
	}
	
	private String getEnd() {
		return end;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter == null ? "" : delimiter;
	}

	public String getDelimiter() {
		return delimiter;
	}

}
