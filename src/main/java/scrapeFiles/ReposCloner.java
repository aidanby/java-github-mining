package scrapeFiles;

import java.io.IOException;
import java.io.PrintWriter;

import read_write.ReadFile;

public class ReposCloner {

	public static void cloneThings(int numRequests, String readFrom, String whichDrive, String whichUser)
			throws InterruptedException, IOException {
		String[] command = { "cmd", };

		String[] repos = ReadFile.reader("//scraper//1000000_Packages_SearchHits_Ruby.txt", numRequests, whichUser);

		int count = 0;
		for (String fullName : repos) {

			if (count < 1500) {
				Process p = Runtime.getRuntime().exec(command);
				new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
				new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
				PrintWriter stdin = new PrintWriter(p.getOutputStream());
				// ***************Write cmd code here*********************//
				stdin.println(whichDrive + ":");
				stdin.println("cd cloned_searchFiles_rb");
				System.out.println("cloning " + fullName);
				stdin.println("git clone https://github.com/" + fullName);
				stdin.println("cd " + fullName.substring(fullName.lastIndexOf("/") + 1));
				String fullName2 = fullName.replace("/", "-");
				stdin.println("git log --name-status > ..\\" + fullName2 + "_commits.txt");
				stdin.println("git log -p > ..\\" + fullName2 + "_patches.diff");
				// ***************Write cmd code here*********************//
				stdin.close();
				Thread.sleep(1000);
				count++;
			}
		}
		System.out.println("Step 3 done");
	}
}
