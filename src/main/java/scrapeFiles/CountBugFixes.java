package scrapeFiles;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import read_write.ReadFile;
import read_write.WriteFile;

public class CountBugFixes {
	public static void count(int numRequests, String whichUser) throws IOException {
		Pattern p = Pattern.compile("^([ADMR]\t)");
		Pattern p2 = Pattern.compile("^([R]\\d)");
		String[] repos = ReadFile.reader("reposNames-nonBDD.txt", numRequests, whichUser);
		for (String repo : repos) {

			String[] lines;
			try {
				lines = ReadFile.reader("rawCommit_nonBDD_java\\" + repo.replaceAll("/", "-") + ".txt", numRequests,
						whichUser);

				int bug = 0;
				int allCommits = 0;
				for (int i = lines.length - 1; i >= 0; i--) {
					if (lines[i].contains("Commit ID: ")) {
						for (int j = i + 3; j < lines.length; j++) {
							Matcher m = p.matcher(lines[j]);
							Matcher m2 = p2.matcher(lines[j]);
							if (!(m.find() || m2.find())) {
								if (lines[j].contains(" fix ") || lines[j].contains(" bug ")
										|| lines[j].contains(" defect ") || lines[j].contains(" solved ")) {
									bug++;
									allCommits++;
								}
							} else {
								allCommits++;
							}

							if (lines[j].contains("BDD:"))
								break;
						}
					}
				}
				float percentage = ((float) bug / (float) allCommits) * 100;
				String percentageLine = percentage + "%";

				if (percentage > 0) {
					System.out.println(repo + "," + bug + "," + allCommits + "," + percentageLine);
					String output = repo + "," + bug + "," + allCommits + "," + percentageLine;
					// WriteFile.writeString(numRequests, output,
					// "issueReports\\issues_nonBDD_commitPercentages.csv",
					// whichUser);
				}

			} catch (Exception e) {
				continue;
			}

		}
	}
}