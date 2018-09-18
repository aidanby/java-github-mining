package NLP;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Helpers {
	public static String getCodeLine(String[] lines, int idx) {
		List<String> list = Arrays.asList(lines[idx].split("\",\""));
		int index = list.size() - 1;
		return list.get(index);
	}

	public static String getFile(String[] lines, int idx) {
		List<String> list = Arrays.asList(lines[idx].split("\",\""));
		int index = list.size() - 2;
		return list.get(index).replace(" ", "");
	}

	public static Date getDate(String[] lines, int idx) throws ParseException {
		List<String> list = Arrays.asList(lines[idx].split("\",\""));
		int index = list.size() - 3;
		String line = list.get(index);
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date date = simpleDateFormat.parse(line);
		return date;
	}
	
	

	public static String getStringDate(String[] lines, int idx) throws ParseException {
		List<String> list = Arrays.asList(lines[idx].split("\",\""));
		int index = list.size() - 3;
		String line = list.get(index);
		return line;
	}

	public static String getAuthor(String[] lines, int idx) {
		List<String> list = Arrays.asList(lines[idx].split("\",\""));
		int index = list.size() - 4;
		return list.get(index);
	}

	public static String getCommitID(String[] lines, int idx) {
		List<String> list = Arrays.asList(lines[idx].split("\",\""));
		int index = list.size() - 5;
		return list.get(index).replaceAll(" ", "");
	}

	public static String getReposName(String[] lines, int idx) {
		List<String> list = Arrays.asList(lines[idx].split("\",\""));
		int index = list.size() - 6;
		return list.get(index);
	}
	
}
