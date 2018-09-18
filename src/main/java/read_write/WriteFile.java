package read_write;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WriteFile {
	public static void write(int numRequests, ArrayList<String> arr, String writeTo, String whichUser) {
		File newFile = new File("C:/Users/" + whichUser + "/Desktop/GitHub_Scraper/" + numRequests + "/" + writeTo);
		try {
			newFile.createNewFile();
			FileWriter fWriter;
			fWriter = new FileWriter(newFile);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			for (String str : arr) {
				bWriter.write(str + "\n");
			}
			bWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeSearchCommits(int numRequests, ArrayList<String> commitBlocks, String writeTo,
			String searchFor, String whichUser) {
		Pattern p = Pattern.compile("([ADMR]\t)");
		File newFile = new File(
				"C:/Users/" + whichUser + "/Desktop/GitHub_Scraper/" + numRequests + "/rawCommit_BDD_rb/" + writeTo);
		try {
			newFile.createNewFile();
			FileWriter fWriter;
			fWriter = new FileWriter(newFile);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			for (String commitBlock : commitBlocks) {
				Matcher m = p.matcher(commitBlock);
				if (commitBlock.contains("." + searchFor + "\n") && m.find()) {
					bWriter.write(commitBlock + "\nBDD: true\n");
				} else {
					bWriter.write(commitBlock + "\nBDD: false\n");
				}
			}
			bWriter.close();
			System.out.println(writeTo + " written");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeFileCSV(int numRequests, List<Integer> fileSize, List<String> commitId, List<String> BDD,
			List<List<String>> files, int totalFiles, int bddFiles, String percentage, String writeTo, String whichUser,
			String userName, String whichDrive) {
		File newFile = new File(
				"C:/Users/" + whichUser + "/Desktop/GitHub_Scraper/" + numRequests + "/fileCSV_rb/" + writeTo);
		try {
			newFile.createNewFile();
			FileWriter fWriter;
			fWriter = new FileWriter(newFile);
			BufferedWriter bWriter = new BufferedWriter(fWriter);

			try {
				bWriter.write("username, " + "totalCommits, " + "bddCommits, " + "percentagesOfFeatureCommits, "
						+ "commitid, " + "author, " + "authorExp, " + "date, " + "bdd, " + "commitSize, "
						+ "filesUploaded, " + "changeType, " + "linesOfCodeChanged\n");
				for (int i = 0; i < commitId.size(); i++) {
					bWriter.write(userName + ", ");
					bWriter.write(totalFiles + ", ");
					bWriter.write(bddFiles + ", ");
					bWriter.write(percentage + ", ");
					bWriter.write(commitId.get(i) + ", ");
					bWriter.write(BDD.get(i) + ", ");
					bWriter.write(fileSize.get(i) + ", ");

					for (int j = 0; j < files.get(i).size(); j++) {
						bWriter.write(files.get(i).get(j) + "\n");
						if (j == files.get(i).size() - 1)
							break;
						bWriter.write(userName + ", ");
						bWriter.write(totalFiles + ", ");
						bWriter.write(bddFiles + ", ");
						bWriter.write(percentage + ", ");
						bWriter.write(commitId.get(i) + ", ");
						bWriter.write(BDD.get(i) + ", ");
						bWriter.write(fileSize.get(i) + ", ");
					}
				}
				System.out.println(userName + "  written");
			} catch (ArrayIndexOutOfBoundsException e) {
			}

			bWriter.close();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	public static void writeFeatureOnlyCommits(int numRequests, List<String> arr, String writeTo, String whichUser) {
		File newFile = new File("C:/Users/" + whichUser + "/Desktop/GitHub_Scraper/" + numRequests + writeTo);
		try {
			newFile.createNewFile();
			FileWriter fWriter = new FileWriter(newFile);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			bWriter.write("commitid" + "," + "author" + "," + "date" + "," + "bdd" + "," + "username" + ","
					+ "filesuploaded" + "\n");
			for (String str : arr) {
				bWriter.write(str + "\n");
			}
			bWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writePercent(int numRequests, List<List<String>> fileRates, String writeTo, String whichUser) {
		File newFile = new File("C:/Users/" + whichUser + "/Desktop/GitHub_Scraper/" + numRequests + "/" + writeTo);
		try {
			newFile.createNewFile();
			FileWriter fWriter;
			fWriter = new FileWriter(newFile);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			for (List<String> file : fileRates) {
				for (String rates : file) {
					bWriter.write(rates + "\n");
				}
			}
			bWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeIssue(int numRequests, List<String> commitId, List<String> author, List<String> date,
			List<String> BDD, List<List<String>> files, String writeTo, String whichUser, String userName) {
		File newFile = new File(
				"C:/Users/" + whichUser + "/Desktop/GitHub_Scraper/" + numRequests + "/issuesCSV/" + writeTo);
		try {
			newFile.createNewFile();
			FileWriter fWriter;
			fWriter = new FileWriter(newFile);
			BufferedWriter bWriter = new BufferedWriter(fWriter);

			try {
				bWriter.write("commitid" + "," + "author" + "," + "date" + "," + "bdd" + "," + "username" + ","
						+ "issues" + "\n");
				for (int i = 0; i < commitId.size(); i++) {
					bWriter.write(commitId.get(i) + ",");
					bWriter.write(BDD.get(i) + ",");
					bWriter.write(userName + ",");

					for (int j = 0; j < files.get(i).size(); j++) {
						bWriter.write(files.get(i).get(j) + "\n");
						if (j == files.get(i).size() - 1)
							break;
						bWriter.write(commitId.get(i) + ",");
						bWriter.write(BDD.get(i) + ",");
						bWriter.write(userName + ",");
					}
					bWriter.write("\n");
				}
				// System.out.println(userName + "written");
			} catch (ArrayIndexOutOfBoundsException e) {
			}

			bWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeFilePercentage(String out, String whichUser, int numRequests, String fullName) {
		File newFile = new File("C:/Users/" + whichUser + "/Desktop/GitHub_Scraper/" + numRequests
				+ "/file-percentages/" + fullName + ".csv");
		try {
			newFile.createNewFile();
			FileWriter fWriter;
			fWriter = new FileWriter(newFile);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			bWriter.write(out);
			bWriter.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static void writeString(int numRequests, String output, String writeTo, String whichUser) {
		File newFile = new File("C:/Users/" + whichUser + "/Desktop/GitHub_Scraper/" + numRequests + "/" + writeTo);
		try {
			newFile.createNewFile();
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(newFile, true));
			bWriter.write(output + "\n");
			bWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeStringSSH(int numRequests, String output, String writeTo, String whichUser) {
		File newFile = new File("/home/aidanben/" + writeTo);
		try {
			newFile.createNewFile();
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(newFile, true));
			bWriter.write(output + "\n");
			bWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeSameLine(int numRequests, String output, String writeTo, String whichUser) {
		File newFile = new File("C:/Users/" + whichUser + "/Desktop/GitHub_Scraper/" + numRequests + "/" + writeTo);
		try {
			newFile.createNewFile();
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(newFile, true));
			bWriter.write(output);
			bWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
