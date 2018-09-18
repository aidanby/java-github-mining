package scrapeFiles;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import read_write.ReadFile;
import read_write.WriteFile;

public class ManuelAnalysis {
	public static void get(int numRequests, String whichUser) {

		Pattern p = Pattern.compile("^\t");

		String[] repos = ReadFile.reader("linking\\toManuel.csv", numRequests, whichUser);

		for (String line : repos) {
			String[] tokenizedLine = line.split(",");
			String username = tokenizedLine[0].replaceAll("\"", "");
			String commitidFeature = tokenizedLine[1].replaceAll("\"", "").replaceAll(" ", "");
			String commitidProduction = tokenizedLine[2].replaceAll("\"", "").replaceAll(" ", "");
			String timeDiff = tokenizedLine[3];
			String logFeature = "";
			String logProduction = "";
			String[] rawCommit = ReadFile.reader("rawCommit_BDD_java\\" + username + ".txt", numRequests, whichUser);
			if (username.equals("username"))
				continue;
			for (int i = 0; i < rawCommit.length; i++) {
				if (rawCommit[i].contains("Commit ID") && rawCommit[i].contains(commitidFeature)) {
					for (int j = i + 3; j < rawCommit.length; j++) {

						if (!(rawCommit[j].contains("A\t") || rawCommit[j].contains("M\t")
								|| rawCommit[j].contains("D\t") || rawCommit[j].contains("R0")
								|| rawCommit[j].contains("R1") || rawCommit[j].contains("BDD: ")))
							logFeature += " " + rawCommit[j].replaceAll("     ", "");

						if (rawCommit[j].contains("BDD: "))
							break;
					}
				}
				if (rawCommit[i].contains("Commit ID") && rawCommit[i].contains(commitidProduction)) {

					for (int j = i + 3; j < rawCommit.length; j++) {

						if (!(rawCommit[j].contains("A\t") || rawCommit[j].contains("M\t")
								|| rawCommit[j].contains("D\t") || rawCommit[j].contains("R0")
								|| rawCommit[j].contains("R1") || rawCommit[j].contains("BDD: ")))
							logProduction += " " + rawCommit[j].replaceAll("     ", "");

						if (rawCommit[j].contains("BDD: "))
							break;
					}
				}
			}
			String output = username + ", " + commitidFeature + ", " + commitidProduction + ", " + timeDiff + ", \""
					+ logFeature + "\", \"" + logProduction + "\"";

			System.out.println(output);
			WriteFile.writeString(numRequests, output, "linking\\logs.csv", whichUser);

		}
	}
}
