package scrapeFiles;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import org.json.JSONException;
import NLP.CoreNLP;
import NLP.FindLinks;
import read_write.WriteFile;

public class Controller {

	static String token[] = { "f25cee6ce53f3b7445658f4d2f3ef992e060b1b5", "8f2e0a31ec4bec6614356b354183e69782d6011a",
			"79f0a1bbbf85d29885454a597a70458222aa3129", "215b49885803bdaf848c8db4d2ffe01be7f5e665",
			"488947a8747d1ef2b4d1472b5174369f0290d8b9", "941158ef5dce357a4a5ed182529ca1b0ae1711c4",
			"8243fd30812a75aaa7583903e51c6e296176fb2e", "0ccebabfbb7db5e14dc1e8ed6d1a0d5eae5e2b94",
			"7c3e8eb0a8726b989af14285ff5952855c18ec4d", "ccba514dddb2a6eadf2fb91aaf86ba93ebd8f99e",
			"7a4a13f5d9d9c87de96a9fed98652de61e875822", "a61823bd2a72abcdc412c16b94c7ce4b4480324a",
			"a0491f8f22bbd3a39dc9a44146de1c99a4b55a8c", "20a5f780aa5b69a5815a614dbe832280fb0e6430",
			"9a98388d32ea4c659a8804b4ef36e8bb0fa3636f", "ce24df085ac87272098c0767aa02c7e9319af084",
			"e7b57349f7e1a18322cdb89c13c3999639c2bc2a", "b0583253876270c54c71e83af8a278934465cd34",
			"f06c8aa9e082bfa476f63b97ae90d96e1001ba9f", "11e19141ae131786699eeed01eb6dc28dfc6ebbc",
			"c736e0a60b88389c74e32d9ed3d2b5be5f7a886f", "8c52a0378e70e5940672305cdb7308a61632e1c7",
			"9b856f398a447fc00cac7aabdcdf42e28d781aac" };

	static int numRequests = 1000000;

	static String packageNames = numRequests + "_Packages_FileNames_Ruby.txt";
	static String searchHits = numRequests + "_Packages_SearchHits_Ruby.txt";
	static String commitFile = numRequests + "_Packages_Search Commits_";
	static String readFrom = "reposNames-BDD.txt";
	static String readFromNonBdd = "reposNames-NonBdd.txt";
	static String readFromRb = "reposNames-rb-BDD.txt";
	static String language = "ruby"; // change to ruby now!
	static String searchFor = "feature";
	static String whichDrive = "K"; // F for PC, D for laptop, K for lab
	static String whichUser = "happyuser"; // aidan for PC, Dell for laptop, happyuser for lab

	static int since = 0; // 0 for first time

	public static void main(String[] args) throws IOException, JSONException, InterruptedException, ParseException {
		int step = 12;

		switch (step) {
		case 1:
			GetFiles.get(numRequests, packageNames, language, whichUser, since);
			// Thread.sleep(30 * 60 * 1000);
			GetSearch.get(numRequests, packageNames, searchHits, searchFor, whichUser);
			break;
		case 2:
			ReposCloner.cloneThings(numRequests, searchHits, whichDrive, whichUser);
			break;

		case 3:
			GetCommits.get(numRequests, searchHits, commitFile, whichDrive, searchFor, whichUser);
			break;

		case 4:
			// ToCSV.getFiles(numRequests, searchHits, commitFile, whichDrive, searchFor,
			// whichUser);
			ToCSV.ruleToDataBase(numRequests, whichUser, readFrom);
			break;

		case 5:
			try {
				DataBaseConnection.fileDataBaseConn(numRequests, whichUser, readFromRb);
			} catch (SQLException e) {
				// System.out.println(e);
			}
			break;

		case 6:
			try {
				DataBaseConnection.codeDataBaseConn(numRequests, whichUser, readFromRb);
			} catch (SQLException e) {
				System.out.println(e);
			}
			break;

		case 7:
			try {
				DataBaseConnection.fileDataBaseConnRules(numRequests, whichUser, readFrom);
			} catch (SQLException e) {
				System.out.println(e);
			}
			break;

		case 8:
			try {
				DataBaseConnection.codeDataBaseConnRules(numRequests, whichUser, "code-toR-clean.txt");
			} catch (SQLException e) {
				System.out.println(e);
			}
			break;

		case 9:
			ToR.fromPython(numRequests, searchHits, whichDrive, whichUser);

		case 10:
			CoreNLP.clean(numRequests, whichUser);
			break;

		case 11:
			FindLinks.findLinks(numRequests, whichUser);
			break;

		case 12:
			CountLOC.countTotal(numRequests, whichUser);
			break;
		case 13:
			FindAttributes.issueReports_nonBDD(numRequests, whichUser, readFrom);
			break;

		case 14:
			ModelTable.make(numRequests, whichUser);
			break;
		case 15:
			ManuelAnalysis.get(numRequests, whichUser);
			break;
		case 16:
			CountBugFixes.count(numRequests, whichUser);
			break;
		}
	}
}
