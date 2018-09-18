package NLP;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import read_write.ReadFile;
import read_write.WriteFile;

public class FindLinks {

	public static void findLinks(int numRequests, String whichUser) throws ParseException {

		String[] lines = ReadFile.reader("linking\\processedCodes-Current.csv", numRequests, whichUser);

		String header = "reposName, dateFeatureFile, developerFeatureFile, commitidFeatureFile, featurefile, dateProductionFile, "
				+ "developerProductionFile, commitidProductionFile, productionFile, timeDifference, similarity";

		WriteFile.writeString(numRequests, header, "linking\\links_allCommits3.csv", whichUser);

		List<String> commitList = new ArrayList<>();
		for (int findFeatureIdx = 0; findFeatureIdx < lines.length; findFeatureIdx++) {
			String reposOuterLoop = Helpers.getReposName(lines, findFeatureIdx);
			String commitIDOuterLoop = Helpers.getCommitID(lines, findFeatureIdx);
			String authorOuterLoop = Helpers.getAuthor(lines, findFeatureIdx);

			if (!commitList.contains(commitIDOuterLoop)) {
				commitList.add(commitIDOuterLoop);
				boolean sameCommit = true;
				int commitLoopIdx = findFeatureIdx;
				List<String> bddFiles = new ArrayList<>();
				while (sameCommit == true && commitLoopIdx < lines.length - 1) {
					String fileNameCommitLoop = Helpers.getFile(lines, commitLoopIdx);
					String commitIDCommitLoop = Helpers.getCommitID(lines, commitLoopIdx);
					if (commitIDCommitLoop.contains(commitIDOuterLoop)) {
						List<String> featureWords = new ArrayList<>();
						int featureSearchIdx = commitLoopIdx;
						Date dateFeatureLoop = Helpers.getDate(lines, featureSearchIdx);
						String dateFeatureFile = Helpers.getStringDate(lines, featureSearchIdx);
						if (fileNameCommitLoop.contains(".feature")) {
							if (!bddFiles.contains(fileNameCommitLoop)) {
								bddFiles.add(fileNameCommitLoop);
								boolean isSameFeatureFile = true;
								while (isSameFeatureFile == true && featureSearchIdx < lines.length - 1) {
									String codeFeatureLoop = Helpers.getCodeLine(lines, featureSearchIdx);
									String fileNameFeatureLoop = Helpers.getFile(lines, featureSearchIdx);
									dateFeatureLoop = Helpers.getDate(lines, featureSearchIdx);
									if (fileNameFeatureLoop.equals(fileNameCommitLoop)) {
										if (codeFeatureLoop != "NULL")
											featureWords.add(codeFeatureLoop);
									} else {
										isSameFeatureFile = false;
									}
									featureSearchIdx++;
								}
							}
						}
						if (!featureWords.isEmpty()) {
							// This is after .feature file found and all code in array "featurewords"

							// foreward
							int commitLinkIdx = findFeatureIdx;
							List<String> codeFiles = new ArrayList<>();
							boolean sameRepos = true;
							while (commitLinkIdx < lines.length - 1 && sameRepos) {
								String reposNameCommitLoop = Helpers.getReposName(lines, commitLinkIdx);
								String fileNameLinkCommitLoop = Helpers.getFile(lines, commitLinkIdx);
								String commitIDLinkCommitLoop = Helpers.getCommitID(lines, commitLinkIdx);
								String authorLinkCommitLoop = Helpers.getAuthor(lines, commitLinkIdx);
								String dateProductionCode = Helpers.getStringDate(lines, commitLinkIdx);
								Date dateLinkCommitLoop = Helpers.getDate(lines, commitLinkIdx);

								long featureCodeTime = dateFeatureLoop.getTime();
								long prodCodeTime = dateLinkCommitLoop.getTime();

								int difference = (int) Math.abs(((prodCodeTime - featureCodeTime) / 1000) / 60);

								if (reposNameCommitLoop.equals(reposOuterLoop) && difference < 10080) {
									linkSearcher(difference, dateFeatureFile, dateProductionCode, commitLinkIdx,
											commitIDOuterLoop, lines, featureWords, reposOuterLoop, authorOuterLoop,
											fileNameCommitLoop, fileNameLinkCommitLoop, codeFiles,
											commitIDLinkCommitLoop, authorLinkCommitLoop, numRequests, whichUser);

								} else
									sameRepos = false;
								commitLinkIdx++;
							}
							// backwards
							commitLinkIdx = findFeatureIdx;
							sameRepos = true;
							while (commitLinkIdx >= 1 && sameRepos) {
								String reposNameCommitLoop = Helpers.getReposName(lines, commitLinkIdx);
								String fileNameLinkCommitLoop = Helpers.getFile(lines, commitLinkIdx);
								String commitIDLinkCommitLoop = Helpers.getCommitID(lines, commitLinkIdx);
								String authorLinkCommitLoop = Helpers.getAuthor(lines, commitLinkIdx);
								String dateProductionCode = Helpers.getStringDate(lines, commitLinkIdx);
								Date dateLinkCommitLoop = Helpers.getDate(lines, commitLinkIdx);

								long featureCodeTime = dateFeatureLoop.getTime();
								long prodCodeTime = dateLinkCommitLoop.getTime();

								int difference = (int) Math.abs(((prodCodeTime - featureCodeTime) / 1000) / 60);

								if (reposNameCommitLoop.equals(reposOuterLoop) && difference < 10080) {
									linkSearcher(difference, dateFeatureFile, dateProductionCode, commitLinkIdx,
											commitIDOuterLoop, lines, featureWords, reposOuterLoop, authorOuterLoop,
											fileNameCommitLoop, fileNameLinkCommitLoop, codeFiles,
											commitIDLinkCommitLoop, authorLinkCommitLoop, numRequests, whichUser);
								} else
									sameRepos = false;
								commitLinkIdx--;
							}
						}
					} else
						sameCommit = false;
					commitLoopIdx++;
				}
			}
		}
		System.out.println("done");
	}

	public static void linkSearcher(int difference, String featureCodeTime, String prodCodeTime, int commitLinkIdx,
			String commitIDOuterLoop, String[] lines, List<String> featureWords, String reposOuterLoop,
			String authorOuterLoop, String fileNameCommitLoop, String fileNameLinkCommitLoop, List<String> codeFiles,
			String commitIDLinkCommitLoop, String authorLinkCommitLoop, int numRequests, String whichUser)
			throws ParseException {
		List<String> codeFileWords = new ArrayList<>();
		if (!fileNameLinkCommitLoop.contains(".feature")) {
			if (!codeFiles.contains(fileNameLinkCommitLoop)) {
				codeFiles.add(fileNameLinkCommitLoop);
				boolean isSameFile = true;
				int codeLinkIdx = commitLinkIdx;
				while (isSameFile == true) {
					String codeLinkLoop = Helpers.getCodeLine(lines, codeLinkIdx);
					String fileNameLinkLoop = Helpers.getFile(lines, codeLinkIdx);
					if (fileNameLinkLoop.contains(fileNameLinkCommitLoop)) {

						String cleanedSentence = codeLinkLoop;

						if (cleanedSentence != "NULL")
							codeFileWords.add(cleanedSentence);
					} else {
						isSameFile = false;
					}
					codeLinkIdx++;
				}
			}
		}
		String codeFileLine = ((String) "" + codeFileWords).replace("[", "").replace("]", "").replaceAll("\"", "");
		String featureFileLine = ((String) "" + featureWords).replace("[", "").replace("]", "").replaceAll("\"", "");
		if (!codeFileWords.isEmpty()) {
			Map<CharSequence, Integer> leftVector = Arrays.stream(codeFileLine.split(","))
					.collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));
			Map<CharSequence, Integer> rightVector = Arrays.stream(featureFileLine.split(","))
					.collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));
			Double howSimilar = 1 - CosineSimilarity.cosineSimilarity(leftVector, rightVector);
			if (howSimilar > 0.8) {
				String output = (reposOuterLoop + "\", \"" + featureCodeTime + "\", \"" + authorOuterLoop + "\", \""
						+ commitIDOuterLoop + "\", \"" + fileNameCommitLoop + "\", \"" + prodCodeTime + "\", \"" + authorLinkCommitLoop + "\", \""
						+ commitIDLinkCommitLoop + "\", \"" + fileNameLinkCommitLoop + "\", " + difference + ", " + String.format("%.2f", howSimilar));
				System.out.println(output);
				WriteFile.writeString(numRequests, output, "linking\\links_allCommits3.csv", whichUser);
			}

		}
	}
}
