package scrapeFiles;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import read_write.ReadFile;
import read_write.WriteFile;

public class CountLOC {
	public static void count(int numRequests, String whichUser) throws IOException {
		// *************** change between nonBDD and BDD here****************
		String[] lines = ReadFile.reader("LOC_analysis\\allLOC_BDD.csv", numRequests, whichUser);

		ArrayList<String> usernames = new ArrayList<>();
		for (int i = 1; i < lines.length; i++) {
			List<String> list = Arrays.asList(lines[i].split(","));
			int codeTypeIdx = list.size() - 1;
			int locIdx = list.size() - 2;
			int fileIdx = list.size() - 3;
			int usernameIdx = list.size() - 5;

			String codeType = list.get(codeTypeIdx).replaceAll("\"", "");
			String LOC = list.get(locIdx).replaceAll("\"", "");
			String fileName = list.get(fileIdx).replaceAll("\"", "");
			String username = list.get(usernameIdx).replaceAll("\"", "");
			int loc = Integer.valueOf(LOC);
			if (usernames.contains(username))
				continue;
			usernames.add(username);

			ArrayList<Integer> outputSource = new ArrayList<>();
			ArrayList<Integer> outputTest = new ArrayList<>();
			ArrayList<Integer> outputDifference = new ArrayList<>();
			int codeLOC = 0;
			int testLOC = 0;

			for (int j = i; j < lines.length; j++) {
				List<String> listUnder = Arrays.asList(lines[j].split(","));
				String codeTypeUnder = listUnder.get(codeTypeIdx).replaceAll("\"", "");
				String LOCUnder = listUnder.get(locIdx).replaceAll("\"", "");
				String fileNameUnder = listUnder.get(fileIdx).replaceAll("\"", "");
				String usernameUnder = listUnder.get(usernameIdx).replaceAll("\"", "");
				int locUnder = Integer.valueOf(LOCUnder);

				if (!usernameUnder.equals(username)) {
					break;
				}

				if (fileNameUnder.contains("src/main") && !fileNameUnder.contains("src/test")) {
					if (codeTypeUnder.contains("A"))
						codeLOC += locUnder;
					else if (codeTypeUnder.contains("D"))
						codeLOC -= locUnder;
					outputSource.add(codeLOC);
				} else if (!fileNameUnder.contains("src/main") && fileNameUnder.contains("src/test")
						&& fileNameUnder.contains(".java")) {
					if (codeTypeUnder.contains("A"))
						testLOC += locUnder;
					else if (codeTypeUnder.contains("D"))
						testLOC -= locUnder;
					outputTest.add(testLOC);
				} else {
					continue;
				}

				outputDifference.add(codeLOC - testLOC);
			}

			// *************** change between nonBDD and BDD here****************
			File newFile = new File("C:/Users/" + whichUser + "/Desktop/GitHub_Scraper/" + numRequests
					+ "/LOC_analysis/allLOC_BDD_difference_toR.csv");
			newFile.createNewFile();
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(newFile, true));

			if (outputSource.size() > 40) {
				for (int k = 0; k < 40; k++) {
					bWriter.write(outputDifference.get(k) + ", ");
				}
				bWriter.write(outputSource.get(39) + "\n");
				bWriter.close();
			}
		}
	}

	public static void countTotal(int numRequests, String whichUser) throws IOException {
		String[] lines = ReadFile.reader("LOC_analysis\\allLOC_BDD.csv", numRequests, whichUser);

		ArrayList<String> usernames = new ArrayList<>();
		for (int i = 1; i < lines.length; i++) {
			List<String> list = Arrays.asList(lines[i].split(","));
			int codeTypeIdx = list.size() - 1;
			int locIdx = list.size() - 2;
			int fileIdx = list.size() - 3;
			int usernameIdx = list.size() - 5;

			String codeType = list.get(codeTypeIdx).replaceAll("\"", "");
			String LOC = list.get(locIdx).replaceAll("\"", "");
			String fileName = list.get(fileIdx).replaceAll("\"", "");
			String username = list.get(usernameIdx).replaceAll("\"", "");
			int loc = Integer.valueOf(LOC);
			if (usernames.contains(username))
				continue;
			usernames.add(username);

			ArrayList<Float> outputFinal = new ArrayList<>();
			int codeLOC = 0;
			int testLOC = 0;
			int count = 1;

			for (int j = i; j < lines.length; j++) {

				List<String> listUnder = Arrays.asList(lines[j].split(","));
				String codeTypeUnder = listUnder.get(codeTypeIdx).replaceAll("\"", "");
				String LOCUnder = listUnder.get(locIdx).replaceAll("\"", "");
				String fileNameUnder = listUnder.get(fileIdx).replaceAll("\"", "");
				String usernameUnder = listUnder.get(usernameIdx).replaceAll("\"", "");
				int locUnder = Integer.valueOf(LOCUnder);

				if (!usernameUnder.equals(username)) {
					break;
				}

				if (fileNameUnder.contains("src/main") && !fileNameUnder.contains("src/test")) {
					if (codeTypeUnder.contains("A"))
						codeLOC += locUnder;
					else if (codeTypeUnder.contains("D"))
						codeLOC -= locUnder;
				} else if (!fileNameUnder.contains("src/main") && fileNameUnder.contains("src/test")
						&& fileNameUnder.contains(".java")) {
					if (codeTypeUnder.contains("A"))
						testLOC += locUnder;
					else if (codeTypeUnder.contains("D"))
						testLOC -= locUnder;
				} else {
					continue;
				}
				count++;
				outputFinal.add((float) testLOC / ((float) codeLOC + (float) testLOC));
			}
			float sum = 0;
			for (float output : outputFinal) {
				sum += output;
			}
			float fin = sum / (float) count;

			WriteFile.writeString(numRequests, username + ", " + fin, "LOC_analysis//normalizedLOC_BDD.csv", whichUser);
			System.out.println(username + ", " + fin);
		}
	}
}

// ArrayList<String> outputFinal = new ArrayList<>();
//
// String header = "username, normalizedLOC";
// WriteFile.writeString(numRequests, header,
// "LOC_analysis//normalizedLOC_BDD.csv", whichUser);
// ArrayList<String> usernames = new ArrayList<>();
// for (int i = 1; i < lines.length; i++) {
// int codeLOC = 0;
// int testLOC = 0;
// List<String> list = Arrays.asList(lines[i].split(","));
// int codeTypeIdx = list.size() - 1;
// int locIdx = list.size() - 2;
// int fileIdx = list.size() - 3;
// int dateIdx = list.size() - 4;
// int usernameIdx = list.size() - 5;
// String codeType = list.get(codeTypeIdx).replaceAll("\"", "");
// String LOC = list.get(locIdx).replaceAll("\"", "");
// String fileName = list.get(fileIdx).replaceAll("\"", "");
// String username = list.get(usernameIdx).replaceAll("\"", "");
// int loc = Integer.valueOf(LOC);
//
// if (!usernames.contains(username))
// usernames.add(username);
//
// if (usernames.size() > 133)
// break;
//
// if (fileName.contains(".java") && fileName.contains("src")) {
// if (fileName.contains("src/main") && !fileName.contains("src/test") &&
// fileName.contains(".java")) {
// if (codeType.contains("A"))
// codeLOC += loc;
// else if (codeType.contains("D"))
// codeLOC += loc;
//
// } else if (!fileName.contains("src/main") && fileName.contains("src/test")
// && fileName.contains(".java")) {
// if (codeType.contains("A"))
// testLOC += loc;
// else if (codeType.contains("D"))
// testLOC += loc;
// } else {
// continue;
// }
// outputFinal.add(username + ", " + (float) testLOC / ((float) codeLOC +
// (float) testLOC));
// }
// }
// for (String output : outputFinal) {
// WriteFile.writeString(numRequests, "" + output,
// "LOC_analysis//normalizedLOC_BDD.csv", whichUser);
// System.out.println(output);
// }
// }
// }
