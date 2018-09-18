package scrapeFiles;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import read_write.WriteFile;

public class GetFiles {
	public static void get(int numRequests, String writeTo, String language, String whichUser, int since)
			throws JSONException, IOException {
		boolean done = false;
		long startTime = System.currentTimeMillis();
		int requests = 0;

		while (!done) {
			for (int tokenIdx = 0; tokenIdx < 23; tokenIdx++) {
				JSONArray json = new JSONArray();
				json = Helper.readJsonArrayFromUrl("https://api.github.com/repositories?since=" + since
						+ "&access_token=" + Controller.token[tokenIdx]);
				// System.out.println("Using token " + tokenIdx + ": " +
				// Controller.token[tokenIdx]);

				requests++;
				for (int pageIdx = 0; pageIdx < 100; pageIdx++) {
					JSONObject obj = json.getJSONObject(pageIdx);
					String fullName = obj.get("full_name").toString();
					try {
						JSONObject json2 = new JSONObject();

						json2 = Helper.readJsonFromUrl("https://api.github.com/repos/" + fullName
								+ "/languages?access_token=" + Controller.token[tokenIdx]);
						requests++;

						Iterator<String> keys = json2.keys();

						String largestThing = "";
						int size = 0;
						while (keys.hasNext()) {
							String element = keys.next();
							if (json2.getInt(element) > size) {
								largestThing = element;
								size = json2.getInt(element);
							}
						}
						if (largestThing.equalsIgnoreCase(language)) {
							WriteFile.writeString(numRequests, fullName, "scraper//" + writeTo, whichUser);
						}

						if (requests % 114500 == 0) {
							Helper.waitHour(startTime);
							startTime = System.currentTimeMillis();
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

				JSONObject json3 = (JSONObject) json.get(99);
				int id = (Integer) json3.get("id");
				since = id;
				System.out.println(since);
			}
			if (requests >= numRequests) {
				done = true;
			}

		}

		File newFile = new File("C:/Users/" + whichUser + "/Desktop/GitHub_Scraper/" + numRequests + "/since.txt");
		System.out.println(since);
		try {
			newFile.createNewFile();
			FileWriter fWriter;
			fWriter = new FileWriter(newFile);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			bWriter.write("since = " + since);
			bWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
