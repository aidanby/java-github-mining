package scrapeFiles;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import read_write.ReadFile;
import read_write.WriteFile;

public class DataBaseConnection {
	public static void fileDataBaseConn(int numRequests, String whichUser, String readFrom) throws SQLException {
		String url = "jdbc:postgresql://localhost:5432/bdddb";
		String user = "postgres";
		String password = "Cat698014003";
		Connection Conn = DriverManager.getConnection(url, user, password);
		try {
			Class.forName("org.postgresql.Driver");

			PreparedStatement makeTable = Conn.prepareStatement(
					"CREATE TABLE files_rb (username text,  totalCommits INT, bddCommits INT, percentagesOfFeatureCommits text, commitid text, author text, "
							+ "authorExp int, date text, bdd boolean, commitSize int, filesUploaded text, changeType text, linesOfCodeChanged  int);");

			makeTable.executeQuery();
		} catch (org.postgresql.util.PSQLException e) {
		} catch (ClassNotFoundException e) {
		}

		String[] repos = ReadFile.reader(readFrom, numRequests, whichUser);

		for (String fullName : repos) {
			fullName = fullName.replaceAll("/", "-");
			try {
				PreparedStatement stmt = Conn.prepareStatement("COPY files_rb" + "\r\n" + "FROM \'C:\\Users\\"
						+ whichUser + "\\Desktop\\Github_Scraper\\1000000\\fileCSV_rb\\" + fullName + ".csv\'"
						+ "DELIMITER \',\' CSV HEADER;");
				stmt.executeQuery();
			} catch (org.postgresql.util.PSQLException e) {
				if (e.getMessage().contains("ERROR:")) {
					System.out.println(fullName);
					System.out.println(e.getMessage());
				}
			}
		}
		System.out.println("Done!");
	}

	public static void codeDataBaseConn(int numRequests, String whichUser, String readFrom)
			throws SQLException, IOException {
		String url = "jdbc:postgresql://localhost:5432/bdddb";
		String user = "postgres";
		String password = "Cat698014003";
		int count = 0;
		Connection Conn = DriverManager.getConnection(url, user, password);
		try {
			Class.forName("org.postgresql.Driver");

			PreparedStatement makeTable = Conn
					.prepareStatement("CREATE TABLE code_rb" + " (\r\n" + "    projectname text,\r\n"
							+ "    commitid text,\r\n" + "    author text,\r\n" + "    date text,\r\n"
							+ "   filename text,\r\n" + "modifications text,    codechangetype text\r\n" + ");");

			makeTable.executeQuery();
		} catch (org.postgresql.util.PSQLException e) {
		} catch (ClassNotFoundException e) {
		}

		String[] repos = ReadFile.reader(readFrom, numRequests, whichUser);

		for (String fullName : repos) {
			fullName = fullName.replaceAll("/", "-");
			try {
				PreparedStatement stmt = Conn.prepareStatement("COPY code_rb" + "\r\n" + "FROM \'C:\\Users\\"
						+ whichUser + "\\Desktop\\Github_Scraper\\1000000\\codeCSV_rb\\" + fullName + ".csv\'"
						+ "DELIMITER \',\' CSV HEADER;");
				stmt.executeQuery();
			} catch (org.postgresql.util.PSQLException e) {
				ArrayList<String> output = new ArrayList<>();
				String message = e.getMessage();
				if (message.contains("ERROR: ")) {
					System.out.println(fullName);
					System.out.println(e);
					count++;
					// errorRepos.add(fullName);
					// List<String> list = Arrays.asList(message.split(" "));
					// int errorIdx = 100;
					// for (int lineIdx = 0; lineIdx < list.size(); lineIdx++) {
					// if (list.get(lineIdx).equals("line")) {
					// errorIdx = lineIdx + 1;
					// }
					// }
					//
					// int lineNumber = Integer.valueOf(list.get(errorIdx).replaceAll(":", ""));
					// String[] fixRepos = ReadFile.reader("codeCSV_allCommits//" + fullName +
					// ".csv", numRequests,
					// whichUser);
					// for (int i = 0; i < fixRepos.length; i++) {
					// if (i + 1 != lineNumber) {
					// // this is line i want to get rid of
					// output.add(fixRepos[i]);
					// }
					// }
					// WriteFile.write(numRequests, output, "codeCSV_allCommits//" + fullName +
					// ".csv", whichUser);
				}
			}
		}
		System.out.println("Total errors: " + count);
		System.out.println("data uploaded");
	}

	public static void codeDataBaseConnRules(int numRequests, String whichUser, String readFrom) throws SQLException {
		String url = "jdbc:postgresql://localhost:5432/bdddb";
		String user = "postgres";
		String password = "Cat698014003";
		Connection Conn = DriverManager.getConnection(url, user, password);
		try {
			Class.forName("org.postgresql.Driver");

			PreparedStatement makeTable = Conn.prepareStatement("CREATE TABLE coderules" + " (\r\n" + "    id text,\r\n"
					+ "    rules text,\r\n" + "    support FLOAT,\r\n" + "    confidence FLOAT,\r\n"
					+ "    lift  FLOAT,\r\n" + "  count INT,\r\n" + "  username text\r\n);");
			makeTable.executeQuery();
		} catch (org.postgresql.util.PSQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		String[] repos = ReadFile.reader(readFrom, numRequests, whichUser);

		for (String userName : repos) {
			String fullName = userName.replace("/", "-");
			try {
				PreparedStatement stmt = Conn.prepareStatement("COPY coderules" + "\r\n" + "FROM \'C:\\Users\\"
						+ whichUser + "\\Desktop\\Github_Scraper\\1000000\\code-rules\\" + fullName
						+ "-code-rules.csv\'" + "DELIMITER \',\' CSV HEADER;");
				stmt.executeQuery();
			} catch (Exception e) {
				System.out.println(e);
			}

		}
		System.out.println("Done!");

	}

	public static void fileDataBaseConnRules(int numRequests, String whichUser, String readFrom) throws SQLException {
		String url = "jdbc:postgresql://localhost:5432/bdddb";
		String user = "postgres";
		String password = "Cat698014003";
		Connection Conn = DriverManager.getConnection(url, user, password);
		try {
			Class.forName("org.postgresql.Driver");

			PreparedStatement makeTable = Conn.prepareStatement("CREATE TABLE filerules" + " (\r\n" + "    id text,\r\n"
					+ "    rules text,\r\n" + "    support FLOAT,\r\n" + "    confidence FLOAT,\r\n"
					+ "    lift  FLOAT,\r\n" + "  count INT,\r\n" + "  username text\r\n);");
			makeTable.executeQuery();
		} catch (org.postgresql.util.PSQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		String[] repos = ReadFile.reader(readFrom, numRequests, whichUser);

		for (String userName : repos) {

			try {
				PreparedStatement stmt = Conn.prepareStatement("COPY filerules" + "\r\n" + "FROM \'C:\\Users\\"
						+ whichUser + "\\Desktop\\Github_Scraper\\1000000\\file-rules\\" + userName
						+ "-rules.csv\'" + "DELIMITER \',\' CSV HEADER;");
				stmt.executeQuery();
			} catch (Exception e) {
				System.out.println(e);
			}

		}
		System.out.println("Done!");

	}

}
