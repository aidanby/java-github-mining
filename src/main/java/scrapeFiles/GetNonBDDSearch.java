package scrapeFiles;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import read_write.ReadFile;
import read_write.WriteFile;

public class GetNonBDDSearch {
	public static void findRepoSize(int numRequests, String whichUser, String readFrom, String language)
			throws JSONException, IOException {

		long startTime = System.currentTimeMillis();

		String[] linesSearchHit = ReadFile.reader("scraper//searchLengthNames.txt", numRequests, whichUser);
		String header = "BDDFile, similarNonBDDFile";
		String headerSearch = "NonBDDFile, repoSize, isBDD";
		WriteFile.writeString(numRequests, header, "scraper//BDD-NonBDD-Links.csv", whichUser);
		//WriteFile.writeString(numRequests, headerSearch, "scraper//findRepoSize.csv", whichUser);

		int requests = 0;
		int tokenIdx = 0;

		for (String usernameSearch : linesSearchHit) {
			try {
				JSONObject jsonSearch = Helper.readJsonFromUrl("https://api.github.com/repos/" + usernameSearch
						+ "/git/trees/master?recursive=1&access_token=" + Controller.token[tokenIdx]);
				requests++;
				tokenIdx = (tokenIdx + 1) % 23;

				if (requests % 119900 == 0) {
					Helper.waitHour(startTime);
					startTime = System.currentTimeMillis();
				}

				JSONArray mainTree = (JSONArray) jsonSearch.get("tree");

				// counting how many files

				int treeLengthSearch = 0;
				boolean isBDD = false;
				for (Object treeBranch : mainTree) {
					JSONObject treeLeaf = (JSONObject) treeBranch;
					String type = (String) treeLeaf.get("type");
					String pathName = (String) treeLeaf.get("path");

					if (pathName.contains(".feature"))
						isBDD = true;

					if (type.equals("tree")) {
						String branchUrl = (String) treeLeaf.get("url");
						JSONObject innerTreeSearch = Helper
								.readJsonFromUrl(branchUrl + "?&access_token=" + Controller.token[tokenIdx]);
						requests++;
						tokenIdx = (tokenIdx + 1) % 23;
						if (requests % 119900 == 0) {
							Helper.waitHour(startTime);
							startTime = System.currentTimeMillis();
						}
						JSONArray innerTree = (JSONArray) innerTreeSearch.get("tree");
						treeLengthSearch += innerTree.length();
						System.out.println(treeLengthSearch);
					} else {
						treeLengthSearch++;
						System.out.println(treeLengthSearch);
					}
				}

				String output = usernameSearch + "," + treeLengthSearch + "," + isBDD;
				System.out.println(output);
				WriteFile.writeString(numRequests, output, "scraper//findRepoSize2.csv", whichUser);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void findLinks2(int numRequests, String whichUser, String readFrom, String language) {
		String[] linesRepoSize = ReadFile.reader("scraper//findRepoSize2.csv", numRequests, whichUser);
		String header = "BDDRepo, NonBDDRepo";
		WriteFile.writeString(numRequests, header, "scraper//BDD-NonBDD-Links.csv", whichUser);
		for(int j = 1; j < linesRepoSize.length; j++) {
			List<String> list = Arrays.asList(linesRepoSize[j].split(","));
			int isBDDIdx = list.size() - 1;
			String isBDD = list.get(isBDDIdx);
			int repoSizeIdx = list.size() - 2;
			String stringRepoSize = list.get(repoSizeIdx).replaceAll(" ", "");
			int repoSize = Integer.valueOf(stringRepoSize);
			int reposNameIdx = list.size() - 3;
			String reposName = list.get(reposNameIdx);
		
			if(isBDD.contains("true")) {
				for(int i = 1; i < linesRepoSize.length; i++) {
					List<String> list2 = Arrays.asList(linesRepoSize[i].split(","));

					int isBDDIdx2 = list2.size() - 1;
					String isBDD2 = list2.get(isBDDIdx2);

					int repoSizeIdx2 = list2.size() - 2;
					String stringRepoSize2 = list2.get(repoSizeIdx2).replaceAll(" ", "");
					int repoSize2 = Integer.valueOf(stringRepoSize2);

					int reposNameIdx2 = list2.size() - 3;
					String reposName2 = list2.get(reposNameIdx2);
					
					if(isBDD2.contains("false") && !reposName2.equals(reposName)) {
						if(Math.abs(repoSize2 - repoSize) < 5){
							System.out.println(reposName);
							String output = reposName + ", " + reposName2;
							WriteFile.writeString(numRequests, output, "scraper//BDD-NonBDD-Links.csv", whichUser);
						}
					}
				}
			}
		}
	}
	
	
	public static void findLinks(int numRequests, String whichUser, String readFrom, String language)
			throws JSONException, IOException {

		long startTime = System.currentTimeMillis();
		String[] lines = ReadFile.reader("scraper//FileAttributes.csv", numRequests, whichUser);
		String[] linesRepoSize = ReadFile.reader("scraper//findRepoSize2.csv", numRequests, whichUser);
		int requests = 0;
		int tokenIdx = 0;
		for (int i = 1; i < lines.length; i++) {
			List<String> list = Arrays.asList(lines[i].split(","));
			int locIdx = list.size() - 1;
			String LOC = list.get(locIdx).replaceAll("\"", "").replaceAll(" ", "");
			int loc = Integer.valueOf(LOC);
			int fileIdx = list.size() - 2;
			String stringFile = list.get(fileIdx).replaceAll("\"", "").replaceAll(" ", "");
			int fileNumber = Integer.valueOf(stringFile);
			int devsIdx = list.size() - 3;
			String stringDevs = list.get(devsIdx).replaceAll("\"", "").replaceAll(" ", "");
			int devs = Integer.valueOf(stringDevs);
			int usernameIdx = list.size() - 4;
			String username = list.get(usernameIdx);
			String[] tokensAttribute = username.split("-");
			System.out.println("starting with: " + username);
			JSONObject json = new JSONObject();
			json = Helper.readJsonFromUrl("https://api.github.com/repos/" + username
					+ "/git/trees/master?recursive=1&access_token=" + Controller.token[tokenIdx]);
			tokenIdx = (tokenIdx + 1) % 23;
			requests++;
			JSONArray mainTree = (JSONArray) json.get("tree");

			int treeLengthSearch = 0;

			for (Object treeBranch : mainTree) {
				JSONObject treeLeaf = (JSONObject) treeBranch;
				String type = (String) treeLeaf.get("type");
				String pathName = (String) treeLeaf.get("path");
				if (type.equals("tree")) {
					String branchUrl = (String) treeLeaf.get("url");
					JSONObject innerTreeSearch = Helper
							.readJsonFromUrl(branchUrl + "?&access_token=" + Controller.token[tokenIdx]);
					requests++;
					tokenIdx = (tokenIdx + 1) % 23;
					if (requests % 119900 == 0) {
						Helper.waitHour(startTime);
						startTime = System.currentTimeMillis();
					}
					JSONArray innerTree = (JSONArray) innerTreeSearch.get("tree");
					treeLengthSearch += innerTree.length();
				} else {
					treeLengthSearch++;
				}
			}
			for (int j = 1; j < linesRepoSize.length; j++) {
				List<String> list2 = Arrays.asList(linesRepoSize[j].split(","));

				int isBDDIdx = list2.size() - 1;
				String isBDD = list2.get(isBDDIdx);

				int repoSizeIdx = list2.size() - 2;
				String stringRepoSize = list.get(repoSizeIdx).replaceAll(" ", "");
				int repoSize = Integer.valueOf(stringRepoSize);

				int reposNameIdx = list2.size() - 3;
				String reposName = list2.get(reposNameIdx);

				if (Math.abs(treeLengthSearch - repoSize) < 20 && !reposName.equals(username) && isBDD.equals(false)) {
					System.out.println(reposName);
					String output = username + ", " + reposName;
					WriteFile.writeString(numRequests, output, "scraper//BDD-NonBDD-Links.csv", whichUser);
				}
			}
		}
	}
}
