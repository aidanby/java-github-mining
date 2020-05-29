package scrapeFiles;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import org.json.JSONException;
import NLP.CoreNLP;
import NLP.FindLinks;
import read_write.WriteFile;

public class Controller {

	static String token[] = { };

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
