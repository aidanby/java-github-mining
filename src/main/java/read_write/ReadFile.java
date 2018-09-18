package read_write;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadFile {
	public static String[] reader(String txtName, int numRequests, String whichUser) {
		File fileName = new File("C:/Users/" + whichUser + "/Desktop/GitHub_Scraper/" + numRequests + "/" + txtName);
		try {
			Scanner sc = new Scanner(fileName);
			List<String> lines = new ArrayList<String>();
			while (sc.hasNextLine()) {
				lines.add(sc.nextLine());
			}
			String[] arr = lines.toArray(new String[0]);
			sc.close();
			return arr;
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
		}
		return null;
	}

	public static String[] readerSSH(String txtName, int numRequests, String whichUser) {
		File fileName = new File("/home/aidanben/" + txtName);
		try {
			Scanner sc = new Scanner(fileName);
			List<String> lines = new ArrayList<String>();
			while (sc.hasNextLine()) {
				lines.add(sc.nextLine());
			}
			String[] arr = lines.toArray(new String[0]);
			sc.close();
			return arr;
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
		}
		return null;
	}

}
