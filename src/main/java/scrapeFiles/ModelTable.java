package scrapeFiles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import read_write.ReadFile;
import read_write.WriteFile;

public class ModelTable {

	public static void make(int numRequests, String whichUser) throws ParseException {

		// 3 less than total column is array idx.
		final int arrayIdx = 29;

		String[] variables = ReadFile.reader("modellingTable//variables.csv", numRequests, whichUser);
		String[] links = ReadFile.reader("modellingTable//links.csv", numRequests, whichUser);
		List<String> linkFlags = new ArrayList<>();
		String header = "\"username\",\"commitid\",\"sourcefiles_added\",\"testfiles_added\",\"otherfiles_added\""
				+ ",\"stepdef_added\",\"sourcefiles_mod\",\"testfiles_mod\",\"otherfiles_mod\",\"stepdef_mod\""
				+ ",\"sourcefiles_del\",\"testfiles_del\",\"otherfiles_del\",\"stepdef_del\",\"sourcefiles_renamed\""
				+ ",\"testfiles_renamed\",\"otherfiles_renamed\",\"stepdef_renamed\",\"methods_added\",\"methods_deleted\""
				+ ",\"sourceloc_added\",\"testloc_added\",\"stepdef_loc_added\",\"import_loc_added\",\"otherloc_added\""
				+ ",\"sourceloc_del\",\"testloc_del\",\"stepdef_loc_del\",\"import_loc_del\",\"otherloc_del\",\"authorexp\",\"date\""
				+ ",\"bdd\"";
		WriteFile.writeString(numRequests, header, "modellingTable\\modelTable.csv", whichUser);

		for (int linksIdx = 1; linksIdx < links.length; linksIdx++) {
			String linkLine = links[linksIdx];
			String[] tokenizedLinkLine = linkLine.split("\",\"");
			String commitid1 = tokenizedLinkLine[3];
			String commitid2 = tokenizedLinkLine[7];
			String username = tokenizedLinkLine[0];

			String pattern = "yyyy-MM-dd HH:mm:ss";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

			String stringDate1 = tokenizedLinkLine[1];
			Date date1 = simpleDateFormat.parse(stringDate1);
			if (!commitid1.equals(commitid2)) {
				String BDD = "False";
				String stringDate2 = tokenizedLinkLine[5];
				Date date2 = simpleDateFormat.parse(stringDate2);
				String date = "";
				long numberDate;

				if (date2.getTime() > date1.getTime()) {
					date = stringDate2;
					numberDate = date2.getTime();
				} else {
					date = stringDate1;
					numberDate = date1.getTime();
				}
				if (linkFlags.contains(commitid1) || linkFlags.contains(commitid2)) {
					continue;
				}
				// if there is link between two commits
				// flag these two commits
				linkFlags.add(commitid1);
				linkFlags.add(commitid2);

				String commitList = commitid1 + ";" + commitid2;

				for (int varIdx = 1; varIdx < variables.length; varIdx++) {
					String varLine = variables[varIdx];
					if (varLine.contains(commitid1)) {
						if (varLine.contains("True")) {
							BDD = "True";
						}
					}
					if (varLine.contains(commitid2)) {
						if (varLine.contains("True")) {
							BDD = "True";
						}
					}
				}
				int[] variables1 = addVars(variables, commitid1, arrayIdx);
				int[] variables2 = addVars(variables, commitid2, arrayIdx);
				// adding variables now
				int[] variablesCombined = new int[arrayIdx];
				for (int i = 0; i < variables1.length - 1; i++) {
					variablesCombined[i] = variables1[i] + variables2[i];
					int authorExp = variablesCombined.length - 1;
					variablesCombined[authorExp] = ((Integer.valueOf(variables1[authorExp])) > (Integer
							.valueOf(variables1[authorExp]))) ? variables1[authorExp] : variables2[authorExp];
				}

				// going under
				int linksIdx2 = linksIdx + 1;
				String username_new = username;

				while (username_new.equals(username) && linksIdx2 < links.length) {
					String linkLine2 = links[linksIdx2];
					String[] tokenizedLinkLine2 = linkLine2.split("\",\"");
					String commitid1_new = tokenizedLinkLine2[3];
					String commitid2_new = tokenizedLinkLine2[7];
					String stringDate1_new = tokenizedLinkLine2[1];
					String stringDate2_new = tokenizedLinkLine2[5];
					Date date3 = simpleDateFormat.parse(stringDate1_new);
					Date date4 = simpleDateFormat.parse(stringDate2_new);
					long numberDate3 = date3.getTime();
					long numberDate4 = date4.getTime();

					if (numberDate3 > numberDate4 && numberDate3 > numberDate) {
						date = stringDate1_new;
					} else if (numberDate4 > numberDate3 && numberDate4 > numberDate) {
						date = stringDate2_new;
					}

					username_new = tokenizedLinkLine2[0];

					int[] variables_new = new int[arrayIdx];
					if (commitList.contains(commitid1_new) && !commitList.contains(commitid2_new)
							&& !linkFlags.contains(commitid2_new)) {
						// commitid 2 in new line is the new one to add
						for (int varIdx = 1; varIdx < variables.length; varIdx++) {
							String varLine = variables[varIdx];
							if (varLine.contains(commitid2_new)) {
								if (varLine.contains("True")) {
									BDD = "True";
								}
							}
						}
						variables_new = addVars(variables, commitid2_new, arrayIdx);
						for (int i = 0; i < variables1.length - 1; i++) {
							variablesCombined[i] += variables_new[i];
							int authorExp = variablesCombined.length - 1;
							variablesCombined[authorExp] = ((Integer.valueOf(variables_new[authorExp])) > (Integer
									.valueOf(variablesCombined[authorExp]))) ? variables_new[authorExp]
											: variablesCombined[authorExp];
						}
						linksIdx2++;
						commitList += ";" + commitid2_new;
						linkFlags.add(commitid2_new);

					} else if (!commitList.contains(commitid1_new) && commitList.contains(commitid2_new)
							&& !linkFlags.contains(commitid1_new)) {
						// commitid 1 in new line is the new one to add
						for (int varIdx = 1; varIdx < variables.length; varIdx++) {
							String varLine = variables[varIdx];
							if (varLine.contains(commitid1_new)) {
								if (varLine.contains("True")) {
									BDD = "True";
								}
							}
						}
						variables_new = addVars(variables, commitid1_new, arrayIdx);
						for (int i = 0; i < variables1.length - 1; i++) {
							variablesCombined[i] += variables_new[i];
							int authorExp = variablesCombined.length - 1;
							variablesCombined[authorExp] = ((Integer.valueOf(variables_new[authorExp])) > (Integer
									.valueOf(variablesCombined[authorExp]))) ? variables_new[authorExp]
											: variablesCombined[authorExp];
						}
						linksIdx2++;
						commitList += ";" + commitid1_new;
						linkFlags.add(commitid1_new);
					} else {
						linksIdx2++;
					}
				}
				// go up to 1 less than arrayIdx
				String linkedLine = tokenizedLinkLine[0] + "\",\"" + commitList + "\",\"" + variablesCombined[0]
						+ "\",\"" + variablesCombined[1] + "\",\"" + variablesCombined[2] + "\",\""
						+ variablesCombined[3] + "\",\"" + variablesCombined[4] + "\",\"" + variablesCombined[5]
						+ "\",\"" + variablesCombined[6] + "\",\"" + variablesCombined[7] + "\",\""
						+ variablesCombined[8] + "\",\"" + variablesCombined[9] + "\",\"" + variablesCombined[10]
						+ "\",\"" + variablesCombined[11] + "\",\"" + variablesCombined[12] + "\",\""
						+ variablesCombined[13] + "\",\"" + variablesCombined[14] + "\",\"" + variablesCombined[15]
						+ "\",\"" + variablesCombined[16] + "\",\"" + variablesCombined[17] + "\",\""
						+ variablesCombined[18] + "\",\"" + variablesCombined[19] + "\",\"" + variablesCombined[20]
						+ "\",\"" + variablesCombined[21] + "\",\"" + variablesCombined[22] + "\",\""
						+ variablesCombined[23] + "\",\"" + variablesCombined[24] + "\",\"" + variablesCombined[25]
						+ "\",\"" + variablesCombined[26] + "\",\"" + variablesCombined[27] + "\",\""
						+ variablesCombined[28] + "\",\"" + date + "\",\"" + BDD + "\"";
				System.out.println(linkedLine);
				WriteFile.writeString(numRequests, linkedLine, "modellingTable\\modelTable.csv", whichUser);
			}

		}
		for (int varIdx = 1; varIdx < variables.length; varIdx++) {

			String varLine = variables[varIdx];
			String[] tokenizedVarLine = varLine.split("\",\"");
			String commit = tokenizedVarLine[1].replaceAll("\\s", "");

			if (!linkFlags.contains(commit) && commit.length() == 40) {
				WriteFile.writeString(numRequests, varLine, "modellingTable\\modelTable.csv", whichUser);
				System.out.println(varLine);
				linkFlags.add(commit);
			}
		}
	}

	public static int[] addVars(String[] variables, String commitid, int howManyVariables) {
		int[] variable = new int[howManyVariables];
		for (int varIdx = 1; varIdx < variables.length; varIdx++) {
			String varLine = variables[varIdx];
			String[] tokenizedVarLine = varLine.split(",");

			if (tokenizedVarLine[1].contains(commitid)) {
				for (int i = 2; i <= tokenizedVarLine.length - 3; i++) {
					variable[i - 2] = Integer.valueOf(tokenizedVarLine[i].replaceAll("\"", ""));
				}
			}
		}
		return variable;
	}
}