package scrapeFiles;

import java.util.ArrayList;

import read_write.ReadFile;
import read_write.WriteFile;

public class FilterRules {
	public static void filter(int numRequests, String readFrom, String whichDrive, String searchFor,
			String whichUser) {
		String[] repos = ReadFile.reader(readFrom, numRequests, whichUser);
		
		for (String userName : repos) {
			ArrayList<String> featureRule = new ArrayList<String>();
			String[] lines = ReadFile.reader("\\rules\\" + userName + "-rules.csv", numRequests, whichUser);
			
			for(String line: lines) {
				if(line.contains(".feature}"))
					featureRule.add(line);
			}
			WriteFile.write(numRequests, featureRule, "\\rules_final\\" + userName + ".csv",whichUser);
		}
	}
}
