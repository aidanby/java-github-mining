package scrapeFiles;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import read_write.ReadFile;
import read_write.WriteFile;

public class GetSearch {
	public static void get(int numRequests, String readFrom, String writeTo, String searchFor, String whichUser) {
		long startTime = System.currentTimeMillis();
		ArrayList<String> hasFeature = new ArrayList<>();
		String[] repos = ReadFile.reader("scraper//" + readFrom, numRequests, whichUser);
		int requests = 0;
		int tokenIdx = 0;
		for (String username : repos) {
			boolean found = false;
			JSONObject json = new JSONObject();
			try {
				json = Helper.readJsonFromUrl("https://api.github.com/repos/" + username
						+ "/git/trees/master?recursive=1&access_token=" + Controller.token[tokenIdx]);
				// System.out.println("Using token: " + i + ": " + Controller.token[i]);

				JSONArray treeArr = (JSONArray) json.get("tree");

				for (Object file : treeArr) {
					JSONObject files = (JSONObject) file;
					String pathName = (String) files.get("path");
					if (pathName.contains("." + searchFor)) {
						found = true;
						break;
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			requests++;

			if (found) {
				hasFeature.add(username);
				System.out.println(username);
			}

			// System.out.println(requests);

			if (requests % 119900 == 0) {
				Helper.waitHour(startTime);
				startTime = System.currentTimeMillis();
			}
			tokenIdx++;
			tokenIdx = tokenIdx % 23;
		}
		WriteFile.write(numRequests, hasFeature, "scraper//" + writeTo, whichUser);
		System.out.println("DONE Step 1");
	}
}
