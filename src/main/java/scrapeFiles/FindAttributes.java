package scrapeFiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import read_write.ReadFile;
import read_write.WriteFile;

public class FindAttributes {
	public static void find(int numRequests, String whichUser, String readFrom) {
		String[] repos = ReadFile.reader(readFrom, numRequests, whichUser);
		String header = "username, developersCount, fileCount, codeCount";
		WriteFile.writeString(numRequests, header, "FileAttributes.csv", whichUser);
		for (String name : repos) {
			String[] lines = ReadFile.reader("\\fileCSV\\" + name + ".csv", numRequests, whichUser);
			List<String> fileCount = new ArrayList<>();
			List<String> authorCount = new ArrayList<>();
			int codeCount = 0;
			for (int i = 1; i < lines.length - 1; i++) {
				List<String> list = Arrays.asList(lines[i].split(","));
				int locIdx = list.size() - 1;
				String LOC = list.get(locIdx).replaceAll("\"", "").replaceAll(" ", "");
				int loc = Integer.valueOf(LOC);
				int fileIdx = list.size() - 2;
				String file = list.get(fileIdx).replaceAll("\"", "");
				int authorIdx = list.size() - 7;
				String author = list.get(authorIdx).replaceAll("\"", "");
				codeCount += loc;
				if (!fileCount.contains(file)) {
					fileCount.add(file);
				}
				if (!authorCount.contains(author)) {
					authorCount.add(author);
				}
			}
			for (int i = 0; i < authorCount.size(); i++) {
				String[] words = authorCount.get(i).split(" ");
				for (String word : words) {
					for (int j = i + 1; j < authorCount.size(); j++) {
						String[] words2 = authorCount.get(j).split(" ");
						for (String word2 : words2) {
							if (word2.equals(word)) {
								authorCount.remove(j);
							}
						}
					}
				}
			}
			String output = name.replaceFirst("-", "/") + ", " + authorCount.size() + ", " + fileCount.size() + ", "
					+ codeCount;
			System.out.println(output);
			WriteFile.writeString(numRequests, output, "FileAttributes.csv", whichUser);
		}
	}

	public static void find133nonBDD(int numRequests, String whichUser, String readFrom) {
		String[] nonBDDrepos = ReadFile.reader("//scraper//links.csv", numRequests, whichUser);

		ArrayList<String> bddRepos = new ArrayList<>();

		for (int i = 0; i < nonBDDrepos.length; i++) {
			String[] tokenizedLine = nonBDDrepos[i].split(",");
			String repo = tokenizedLine[1].replaceAll(" ", "");
			String bddRepo = tokenizedLine[0].replaceAll(" ", "");

			if (!bddRepos.contains(bddRepo)) {
				bddRepos.add(bddRepo);
				WriteFile.writeString(numRequests, repo, "reposNames-nonBDD.txt", whichUser);
				System.out.println(repo);
			}
		}
	}

	public static void githubApi(int numRequests, String whichUser, String readFrom) throws JSONException, IOException {
		String[] BDDrepos = ReadFile.reader("//scraper//FileAttributes.csv", numRequests, whichUser);

		int tokenIdx = 0;
		int requests = 0;
		// System.out.println("waiting for an hour");
		// Helper.waitHour(startTime);
		// startTime = System.currentTimeMillis();

		for (int i = 1; i < BDDrepos.length; i++) {
			String[] tokenizedLine = BDDrepos[i].split(",");
			String repo = tokenizedLine[0];
			JSONObject attributeSearch = Helper.readJsonFromUrl(
					"https://api.github.com/repos/" + repo + "?&access_token=" + Controller.token[tokenIdx]);

			tokenIdx = (tokenIdx + 1) % 23;
			requests++;
			if (requests % 650 == 0) {
				System.out.println("waiting for a minute");
				Helper.waitMinute();
			}

			int size = (Integer) attributeSearch.get("size");
			int stars = (Integer) attributeSearch.get("stargazers_count");
			int forks = (Integer) attributeSearch.get("forks_count");

			// start searching repos with size stars forks and language

			String url = "https://api.github.com/search/repositories?q=language:java+size:" + size + "+stars:" + stars
					+ "+forks:" + forks + "&access_token=" + Controller.token[tokenIdx];
			tokenIdx = (tokenIdx + 1) % 23;
			requests++;
			if (requests % 650 == 0) {
				System.out.println("waiting for a minute");
				Helper.waitMinute();
			}
			JSONObject nonBDDSearch;
			int total_count;
			try {
				nonBDDSearch = Helper.readJsonFromUrl(url);
				total_count = (Integer) nonBDDSearch.get("total_count");
			} catch (Exception e1) {
				System.out.println("waiting for a minute");
				Helper.waitMinute();
				nonBDDSearch = Helper.readJsonFromUrl(url);
				total_count = (Integer) nonBDDSearch.get("total_count");
			}
			int sizeOffset = 1;
			try {
				while (total_count < 3 && sizeOffset < 10) {
					size += sizeOffset;
					nonBDDSearch = Helper.readJsonFromUrl(
							"https://api.github.com/search/repositories?q=language:java+size:" + size + "+stars:"
									+ stars + "+forks:" + forks + "&access_token=" + Controller.token[tokenIdx]);
					tokenIdx = (tokenIdx + 1) % 23;
					requests++;
					if (requests % 650 == 0) {
						System.out.println("waiting for a minute");
						Helper.waitMinute();
					}
					total_count = (Integer) nonBDDSearch.get("total_count");
					sizeOffset++;
				}
				sizeOffset = -1;
				while (total_count < 3 && sizeOffset > -10) {
					size -= sizeOffset;
					nonBDDSearch = Helper.readJsonFromUrl(
							"https://api.github.com/search/repositories?q=language:java+size:" + size + "+stars:"
									+ stars + "+forks:" + forks + "&access_token=" + Controller.token[tokenIdx]);
					tokenIdx = (tokenIdx + 1) % 23;
					requests++;
					if (requests % 650 == 0) {
						System.out.println("waiting for a minute");
						Helper.waitMinute();
					}
					total_count = (Integer) nonBDDSearch.get("total_count");
					sizeOffset--;
					if (total_count > 1)
						break;
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println("waiting for a minute");
				Helper.waitMinute();
			}

			JSONArray items = (JSONArray) nonBDDSearch.get("items");

			int linkCount = 0;
			for (Object eachItem : items) {
				linkCount++;
				JSONObject item = (JSONObject) eachItem;
				String nonBDDname = (String) item.get("full_name");
				if (!nonBDDname.equals(repo) && linkCount < 11) {
					String output = repo + ", " + nonBDDname;
					WriteFile.writeString(numRequests, output, "scraper//links.csv", whichUser);
					System.out.println(output);
				}
			}
		}
	}

	public static void pullApi_BDD(int numRequests, String whichUser, String readFrom)
			throws JSONException, IOException {
		String[] BDDrepos = ReadFile.reader("//scraper//BDDReposAttributes.csv", numRequests, whichUser);
		int tokenIdx = 0;
		String header = "project, pullTitle, state, timeDifference";
		WriteFile.writeString(numRequests, header, "pullRequests\\pullRequests_BDD.csv", whichUser);

		for (int i = 1; i < BDDrepos.length; i++) {
			String[] tokenizedLine = BDDrepos[i].split(",");

			String repo = tokenizedLine[0].replaceAll(" ", "");

			try {
				String url = "https://api.github.com/repos/" + repo + "/pulls?state=all&access_token="
						+ Controller.token[tokenIdx];

				JSONArray attributeSearch = Helper.readJsonArrayFromUrl(url);

				tokenIdx = (tokenIdx + 1) % 23;

				for (Object pull : attributeSearch) {

					String title = ((JSONObject) pull).getString("title");
					String state = ((JSONObject) pull).getString("state");

					String difference;
					try {
						String startTime = ((JSONObject) pull).getString("created_at").replaceAll("T", " ")
								.replaceAll("Z", "");
						String endTime = ((JSONObject) pull).getString("merged_at").replaceAll("T", " ").replaceAll("Z",
								"");
						String pattern = "yyyy-MM-dd HH:mm:ss";
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
						Date start = simpleDateFormat.parse(startTime);
						Date end = simpleDateFormat.parse(endTime);
						difference = "" + (Math.abs(start.getTime() - end.getTime()) / 1000) / 60;
						if (state.contains("closed"))
							state = "merged";
					} catch (Exception e) {
						difference = "NA";
						if (state.contains("closed"))
							state = "closed";
					}

					String output = repo.replaceAll("/", "-") + ", \"" + title + "\", " + state + ", " + difference;
					// System.out.println(output);
					WriteFile.writeString(numRequests, output, "pullRequests\\pullRequests_BDD.csv", whichUser);
				}
			} catch (Exception e) {
				System.out.println(repo + ": " + e.getMessage());
			}

		}
	}

	public static void pullApi_nonBDD(int numRequests, String whichUser, String readFrom)
			throws JSONException, IOException {
		String[] nonBDDrepos = ReadFile.reader("reposNames-nonBDD.txt", numRequests, whichUser);
		int tokenIdx = 0;
		int requests = 0;
		String header = "project, pullTitle, state, timeDifference";
		WriteFile.writeString(numRequests, header, "pullRequests\\pullRequests_nonBDD2.csv", whichUser);
		for (int i = 0; i < nonBDDrepos.length; i++) {

			String repo = nonBDDrepos[i];

			try {
				String url = "https://api.github.com/repos/" + repo + "/pulls?state=all&access_token="
						+ Controller.token[tokenIdx];

				JSONArray attributeSearch = Helper.readJsonArrayFromUrl(url);

//				tokenIdx = (tokenIdx + 1) % 3;

				requests++;
//				if (requests % 100 == 0) {
//					System.out.println("waiting for a minute");
//					Helper.waitMinute();
//				}

				for (Object pull : attributeSearch) {

					String title = ((JSONObject) pull).getString("title");
					String state = ((JSONObject) pull).getString("state");

					String difference;
					try {
						String startTime = ((JSONObject) pull).getString("created_at").replaceAll("T", " ")
								.replaceAll("Z", "");
						String endTime = ((JSONObject) pull).getString("merged_at").replaceAll("T", " ").replaceAll("Z",
								"");
						String pattern = "yyyy-MM-dd HH:mm:ss";
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
						Date start = simpleDateFormat.parse(startTime);
						Date end = simpleDateFormat.parse(endTime);
						difference = "" + (Math.abs(start.getTime() - end.getTime()) / 1000) / 60;
						if (state.contains("closed"))
							state = "merged";
					} catch (Exception e) {
						difference = "NA";
						if (state.contains("closed"))
							state = "closed";
					}

					String output = repo.replaceAll("/", "-") + ", \"" + title + "\", " + state + ", " + difference;
					System.out.println(output);
					WriteFile.writeString(numRequests, output, "pullRequests\\pullRequests_nonBDD2.csv", whichUser);
				}
			} catch (Exception e) {
				System.out.println(repo + ": " + e.getMessage());
			}

		}
	}

	public static void issueReports_BDD(int numRequests, String whichUser, String readFrom)
			throws JSONException, IOException {
		String[] BDDrepos = ReadFile.reader("//scraper//BDDReposAttributes.csv", numRequests, whichUser);
		int tokenIdx = 0;
		int requests = 0;
		String header = "project, ErrorTitle, state, timeDifference";
		WriteFile.writeString(numRequests, header, "issueReports\\issues_BDD.csv", whichUser);

		for (int i = 1; i < BDDrepos.length; i++) {
			String[] tokenizedLine = BDDrepos[i].split(",");

			String repo = tokenizedLine[0].replaceAll(" ", "");

			try {
				String url = "https://api.github.com/repos/" + repo + "/issues?state=all&access_token="
						+ Controller.token[tokenIdx];

				JSONArray attributeSearch = Helper.readJsonArrayFromUrl(url);

				tokenIdx = (tokenIdx + 1) % 22;
				requests++;
				if (requests % 650 == 0) {
					System.out.println("waiting for a minute");
					Helper.waitMinute();
				}

				for (Object pull : attributeSearch) {

					String title = ((JSONObject) pull).getString("title");
					String state = ((JSONObject) pull).getString("state");

					String difference;
					try {
						String startTime = ((JSONObject) pull).getString("created_at").replaceAll("T", " ")
								.replaceAll("Z", "");
						String endTime = ((JSONObject) pull).getString("closed_at").replaceAll("T", " ").replaceAll("Z",
								"");
						String pattern = "yyyy-MM-dd HH:mm:ss";
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
						Date start = simpleDateFormat.parse(startTime);
						Date end = simpleDateFormat.parse(endTime);
						difference = "" + (Math.abs(start.getTime() - end.getTime()) / 1000) / 60;
						if (state.contains("closed"))
							state = "fixed";
					} catch (Exception e) {
						difference = "NA";
					}
					String output = repo.replaceAll("/", "-") + ", \"" + title + "\", " + state + ", " + difference;
					System.out.println(output);
					WriteFile.writeString(numRequests, output, "issueReports\\issues_BDD.csv", whichUser);
				}
			} catch (Exception e) {
				System.out.println(repo + ": " + e.getMessage());
			}
		}
	}

	public static void issueReports_nonBDD(int numRequests, String whichUser, String readFrom)
			throws JSONException, IOException {
		String[] nonBDDrepos = ReadFile.reader("reposNames-nonBDD.txt", numRequests, whichUser);
		int tokenIdx = 1;

		String header = "project, ErrorTitle, state, timeDifference";
		WriteFile.writeString(numRequests, header, "issueReports\\issues_nonBDD2.csv", whichUser);



		for (int i = 0; i < nonBDDrepos.length; i++) {
			String repo = nonBDDrepos[i];
			try {
				String url = "https://api.github.com/repos/" + repo + "/issues?state=all&access_token="
						+ Controller.token[tokenIdx];

				JSONArray attributeSearch = Helper.readJsonArrayFromUrl(url);

				

				for (Object pull : attributeSearch) {

					String title = ((JSONObject) pull).getString("title");
					String state = ((JSONObject) pull).getString("state");

					String difference;
					try {
						String startTime = ((JSONObject) pull).getString("created_at").replaceAll("T", " ")
								.replaceAll("Z", "");
						String endTime = ((JSONObject) pull).getString("closed_at").replaceAll("T", " ").replaceAll("Z",
								"");
						String pattern = "yyyy-MM-dd HH:mm:ss";
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
						Date start = simpleDateFormat.parse(startTime);
						Date end = simpleDateFormat.parse(endTime);
						difference = "" + (Math.abs(start.getTime() - end.getTime()) / 1000) / 60;
						if (state.contains("closed"))
							state = "fixed";
					} catch (Exception e) {
						difference = "NA";
					}
					String output = repo.replaceAll("/", "-") + ", \"" + title + "\", " + state + ", " + difference;
					System.out.println(output);
					WriteFile.writeString(numRequests, output, "issueReports\\issues_nonBDD2.csv", whichUser);
				}
			} catch (Exception e) {
				System.out.println(repo + ": " + e.getMessage());
			}

		}
	}
}
