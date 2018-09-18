package scrapeFiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import read_write.ReadFile;
import read_write.WriteFile;

public class GetCommits {
	public static void get(int numRequests, String readFrom, String writeTo, String whichDrive, String searchFor,
			String whichUser) {
		String[] repos = ReadFile.reader("scraper\\" + readFrom, numRequests, whichUser);
		for (String userName : repos) {
			ArrayList<String> commitBlocks = new ArrayList<>();
			String fullName = userName.replace("/", "-");
			try {
				Scanner scanner = new Scanner(
						new File(whichDrive + ":/cloned_searchFiles_rb/" + fullName + "_commits.txt"));
				// initialize the string delimiter
				scanner.useDelimiter("commit ");
				// Printing the tokenized Strings

				while (scanner.hasNextLine()) {
					commitBlocks.add("\nCommit ID: " + scanner.next() + "\n");
				}
				// closing the scanner stream
				scanner.close();
				// System.out.println(hasFeature);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if (commitBlocks.size() >= 1)
				WriteFile.writeSearchCommits(numRequests, commitBlocks, fullName + ".txt", searchFor, whichUser);
		}
		System.out.println("Done");
	}

	public static void getNonBDD(int numRequests, String readFrom, String writeTo, String whichDrive, String searchFor,
			String whichUser) {
		String[] repos = ReadFile.reader("//scraper//links.csv", numRequests, whichUser);
		for (String line : repos) {
			String[] tokenized = line.split(",");
			String userName = tokenized[1].replaceAll("\\s", "");

			ArrayList<String> commitBlocks = new ArrayList<>();
			String fullName = userName.replace("/", "-");
			try {
				Scanner scanner = new Scanner(
						new File(whichDrive + ":/cloned_nonBddFiles/" + fullName + "_commits.txt"));
				// initialize the string delimiter
				scanner.useDelimiter("commit ");
				// Printing the tokenized Strings

				while (scanner.hasNextLine()) {
					commitBlocks.add("\nCommit ID: " + scanner.next() + "\n");
				}
				// closing the scanner stream
				scanner.close();
				// System.out.println(hasFeature);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if (commitBlocks.size() >= 1) {
				WriteFile.writeSearchCommits(numRequests, commitBlocks, fullName + ".txt", searchFor, whichUser);
				WriteFile.writeString(numRequests, fullName, "reposNames-NonBdd.txt", whichUser);
			}
		}
		System.out.println("Done");
	}

}