package scrapeFiles;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import read_write.ReadFile;
import read_write.WriteFile;

public class ToCSV {
	public static void getFiles(int numRequests, String readFrom, String writeTo, String whichDrive, String searchFor,
			String whichUser) throws FileNotFoundException {
		String[] repos = ReadFile.reader("scraper\\" + readFrom, numRequests, whichUser);

		Pattern p = Pattern.compile("^([ADMR]\t)");
		Pattern p2 = Pattern.compile("^([R]\\d)");

		for (String userName : repos) {
			String fullName = userName.replace("/", "-");

			String[] codeCsvLines;
			try {
				codeCsvLines = ReadFile.reader("\\codeCSV_rb\\" + fullName + ".csv", numRequests, whichUser);
				if (codeCsvLines.length < 2)
					continue;
			} catch (Exception e1) {
				continue;
			}

			System.out.println(userName + " start");
			int bddFile = 0;
			int totalFile = 0;

			List<String> commitId = new ArrayList<>();
			List<String> BDD = new ArrayList<>();
			List<Integer> fileSize = new ArrayList<>();
			List<List<String>> files = new ArrayList<>();

			try {
				String[] lines = ReadFile.reader("rawCommit_BDD_rb\\" + fullName + ".txt", numRequests, whichUser);
				if (lines.length < 5)
					continue;

				boolean hasCode = false;

				Hashtable<String, Integer> authorExp = new Hashtable<String, Integer>();

				for (int i = lines.length - 1; i >= 0; i--) {
					List<String> file = new ArrayList<String>();

					if (lines[i].contains("Commit ID: ")) {

						String ID = lines[i].replace("Commit ID: ", "");
						for (int j = i + 3; j < lines.length; j++) {
							Matcher m = p.matcher(lines[j]);
							Matcher m2 = p2.matcher(lines[j]);

							// files modified in commit
							if (m.find() || m2.find()) {
								int codeChangedCount = 0;
								Pattern add = Pattern.compile("^([A]\t)");
								Pattern mod = Pattern.compile("^([M]\t)");
								Pattern del = Pattern.compile("^([D]\t)");
								Pattern rename = Pattern.compile("^([R]\\d\\d)");

								Matcher addMatch = add.matcher(lines[j]);
								Matcher modMatch = mod.matcher(lines[j]);
								Matcher delMatch = del.matcher(lines[j]);
								Matcher renameMatch = rename.matcher(lines[j]);

								boolean isAdd = false;
								boolean isMod = false;
								boolean isDel = false;
								boolean isRename = false;

								if (addMatch.find())
									isAdd = true;
								else if (modMatch.find())
									isMod = true;
								else if (delMatch.find())
									isDel = true;
								else if (renameMatch.find())
									isRename = true;

								String fileName = lines[j];

								if (isRename) {
									fileName = fileName.replaceAll("R\\d\\d\\d", "");
								} else {
									fileName = lines[j].replaceAll("A\t", "").replaceAll("D\t", "")
											.replaceAll("M\t", "").replaceAll("	", "");
								}

								if (!fileName.contains(".txt") && !fileName.contains(".csv")) {
									// if codeCSV file contains commit file name add lines to codeChangedCount
									for (String codeLine : codeCsvLines) {
										if (codeLine.contains(fileName) && codeLine.contains(ID))
											codeChangedCount++;
									}

									if (isAdd)
										file.add(fileName + ", A, " + codeChangedCount);
									else if (isMod)
										file.add(fileName + ", M, " + codeChangedCount);
									else if (isDel)
										file.add(fileName + ", D, " + codeChangedCount);
									else if (isRename) {
										List<String> list = Arrays.asList(fileName.split("\\s"));
										int lastIdx = list.size() - 1;
										file.add(list.get(lastIdx) + ", R, " + codeChangedCount);
									} else
										file.add(fileName + ", NULL, " + codeChangedCount);

									if (codeChangedCount > 0) {
										hasCode = true;
									}
								}

							}

							if (lines[j].contains("BDD:"))
								break;
						}

						if (file.size() < 2)
							continue;

						fileSize.add(file.size());

						files.add(file);

						if (lines[i + 1].contains("Author: ")) {
							String author = lines[i + 1].replace("Author: ", "");
							if (authorExp.containsKey(author)) {
								int previouskey = authorExp.get(author);
								authorExp.put(author, previouskey + 1);
							} else {
								authorExp.put(author, 1);
							}

							commitId.add(lines[i].replace("Commit ID: ", "") + "," + author + ","
									+ authorExp.get(author) + "," + lines[i + 2].replace("Date: ", ""));
						} else if (lines[i + 1].contains("Merge: ")) {
							String author = lines[i + 2].replace("Author: ", "");
							if (authorExp.containsKey(author)) {
								int previouskey = authorExp.get(author);
								authorExp.put(author, previouskey + 1);
							} else {
								authorExp.put(author, 1);
							}
							commitId.add(lines[i].replace("Commit ID: ", "") + "," + author + ","
									+ authorExp.get(author) + "," + lines[i + 3].replace("Date: ", ""));

						}

						for (int BDDFinder = i + 1; BDDFinder < lines.length; BDDFinder++) {
							if (lines[BDDFinder].contains("BDD: ")) {
								BDD.add(lines[BDDFinder].replace("BDD: ", ""));
								break;
							}
						}
					}

					if (lines[i].contains("BDD: ")) {
						totalFile++;
						String line = lines[i];
						if (line.contains("true")) {
							bddFile++;
						}
					}
				}

				float percentage = ((float) bddFile / (float) totalFile) * 100;
				String percentageLine = percentage + "%";
				// for BDD change to "&& percentage > 0"
				if (hasCode == true) {
					WriteFile.writeFileCSV(numRequests, fileSize, commitId, BDD, files, totalFile, bddFile,
							percentageLine, fullName + ".csv", whichUser, fullName, whichDrive);
					WriteFile.writeString(numRequests, userName, "reposNames-rb-BDD.txt", whichUser);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public static void toApriori(int numRequests, String whichUser, String readFrom) {
		String[] repos = ReadFile.reader(readFrom, numRequests, whichUser);
		for (String userName : repos) {
			String header = "username, totalCommits, bddCommits, percentagesOfFeatureCommits, commitid, author, authorExp, date, bdd, commitSize, filesUploaded, changeType, linesOfCodeChanged";
			WriteFile.writeString(numRequests, header, "fileCSV_toApriori\\" + userName + ".csv", whichUser);
			String[] lines = ReadFile.reader("fileCSV\\" + userName + ".csv", numRequests, whichUser);
			for (String line : lines) {
				String[] tokenized = line.split(",");
				String file = tokenized[10];
				if (file.contains(".java") || file.contains(".feature")) {
					if (!file.contains("pom.xml")) {
						System.out.println(line);
						WriteFile.writeString(numRequests, line, "fileCSV_toApriori\\" + userName + ".csv", whichUser);
					}

				}
			}

		}

	}

	public static void pivotFilter(int numRequests, String whichUser, String readFrom) {
		String[] repos = ReadFile.reader(readFrom, numRequests, whichUser);
		for (String userName : repos) {
			String header = ",files";
			WriteFile.writeString(numRequests, header, "fileCSV_pivoted_clean\\" + userName + ".csv", whichUser);
			String[] lines = ReadFile.reader("fileCSV_pivoted\\" + userName + ".csv", numRequests, whichUser);
			for (String line : lines) {
				if (line.contains(".feature")) {
					System.out.println(line);
					WriteFile.writeString(numRequests, line, "fileCSV_pivoted_clean\\" + userName + ".csv", whichUser);
				}
			}
		}
	}

	public static void ruleToDataBase(int numRequests, String whichUser, String readFrom) {
		String[] repos = ReadFile.reader("temp.txt", numRequests, whichUser);
//		String header = "username, index, rules, support, confidence, lift, count";
//		WriteFile.writeString(numRequests, header, "file-rules.csv", whichUser);
		for (String userName : repos) {
			String[] lines;
			try {
				lines = ReadFile.reader("file-rules\\" + userName + "-rules.csv", numRequests, whichUser);
				System.out.println(userName);
				if (lines.length < 2)
					continue;
			} catch (Exception e) {
				continue;
			}

			for (int i = 1; i < lines.length; i++) {
				String output = userName + ", " + lines[i];
				//System.out.println(output);
				WriteFile.writeString(numRequests, output, "file-rules.csv", whichUser);
			}
		}
	}

}
