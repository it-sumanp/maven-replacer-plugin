package bakersoftware.maven_replacer_plugin;

import java.util.List;
import java.util.regex.Pattern;

public class PatternFlagsFactory {


	public int buildFlags(List<String> flags) {
		if (flags == null || flags.isEmpty()) {
			return Pattern.MULTILINE; 
		}
		// TODO Auto-generated method stub
		return 0;
	}
}
