package scrapeFiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import read_write.ReadFile;
import read_write.WriteFile;

public class ToR {
	public static void fromPython(int numRequests, String readFrom, String whichDrive, String whichUser) {
		ArrayList<String> names = new ArrayList<String>();
		String[] repos = ReadFile.reader(readFrom, numRequests, whichUser);

		for (String userName : repos) {
			try {
				ArrayList<String> eachLine = new ArrayList<>();
				String fullName = userName.replace("/", "-");
				String[] lines = ReadFile.reader("\\codeCSV_commitsWithFeature\\" + fullName + ".csv", numRequests, whichUser);
				if (lines.length > 1) {
					names.add(fullName);
					System.out.println(fullName);
					for(String line: lines) {
						if(line.length() > 1) {
							eachLine.add(line);
						}
					}
					WriteFile.write(numRequests, eachLine, "codeCSV_commitsWithFeature_clean\\" + fullName + ".csv", whichUser);
				}
				
			} catch (Exception e) {
			}

		}
		WriteFile.write(numRequests, names, "\\codeCSV_commitsWithFeature_names.txt", whichUser);
	}

	public static void codeCount(int numRequests, String readFrom, String whichDrive, String whichUser) {
		String[] repos = ReadFile.reader(readFrom, numRequests, whichUser);
		for (String userName : repos) {
			ArrayList<String> count = new ArrayList<String>();
			ArrayList<String> commitBlocks = new ArrayList<>();
			String fullName = userName.replace("/", "-");
			
			try {
				Scanner scanner = new Scanner(
						new File("C:/Users/happyuser/Desktop/GitHub_Scraper/1000000/codeCSV/" + fullName + ".csv"));
				// initialize the string delimiter
				scanner.useDelimiter("\\n\\n");

				while (scanner.hasNextLine()) {
					commitBlocks.add("\nCommit ID: " + scanner.next() + "\n");
				}
				scanner.close();
				int c = 0;
				for (String commit : commitBlocks) {
					c++;
					String[] arr = commit.split("\\n");
					count.add("lines of code for commit " + c + ": " + arr.length);
				}
				
				
			} catch (Exception e) {

			}
		WriteFile.write(numRequests, count, "\\code-count\\" + fullName + ".txt", whichUser);
		}

	}
}
