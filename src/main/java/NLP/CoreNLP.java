package NLP;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import read_write.ReadFile;
import read_write.WriteFile;

public class CoreNLP {

	public static void clean(int numRequests, String whichUser) throws ParseException {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		String[] lines = ReadFile.reader("linking\\toNLP_rb.csv", numRequests, whichUser);
		// String[] lines = ReadFile.readerSSH("toNLP.csv", numRequests, whichUser);
		for (int i = 0; i < lines.length; i++) {
			try {
				String code = Helpers.getCodeLine(lines, i);
				String file = Helpers.getFile(lines, i);
				String author = Helpers.getAuthor(lines, i);
				String commitID = Helpers.getCommitID(lines, i);
				String repos = Helpers.getReposName(lines, i);
				String date = Helpers.getStringDate(lines, i);

				System.out.println(lines[i]);
				if (!file.contains(".xml") && !file.contains(".csv") && !file.contains(".tex")
						&& !file.contains("README") && !file.contains("txt")) {

					String cleanedSentence = cleaningSentences(code, file, author, commitID, repos, pipeline);

					String output = repos + "\",\"" + commitID + "\",\"" + author + "\",\"" + date + "\",\"" + file
							+ "\",\"" + cleanedSentence + "\"";
					if (!output.contains("NULL")) {
						WriteFile.writeString(numRequests, output, "linking/processedCodes_rb.csv", whichUser);
						// WriteFile.writeStringSSH(numRequests, output, "processedCodes_rb.csv",
						// whichUser);
						System.out.println(output);
					}
				}
			} catch (Exception e) {
				System.out.println(e);
				System.out.println(lines[i]);
				continue;
			}
		}
	}

	public static String cleaningSentences(String codeLine, String fileName, String author, String commitID,
			String reposName, StanfordCoreNLP pipeline) {

		String[] featureKeys = { "modifications", "Scenario", "Given", "Then", "Feature" };
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(codeLine);
		// run all Annotators on this text
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			List<String> eachSentence = new ArrayList<>();
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
				// this is the text of the token
				String fullWord = token.get(CoreAnnotations.TextAnnotation.class);
				String[] words = StringUtils.splitByCharacterTypeCamelCase(fullWord);

				for (String word : words) {
					word = word.toLowerCase();

					if (word.length() < 2)
						continue;

					try {
						int number = Integer.valueOf(word);
						continue;
					} catch (NumberFormatException e) {
					}
					// this is the POS tag of the token
					String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
					// this is the NER label of the token
					// String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
					boolean isKey = false;
					for (String key : featureKeys) {
						if (key.contains(word))
							isKey = true;
					}

					// word = word.replaceAll("@", "").replaceAll("%", "").replaceAll("=",
					// "").replaceAll("end", "")
					// .replaceAll("/", "").replaceAll("\\", "").replaceAll("[", "").replaceAll("]",
					// "")
					// .replaceAll("*", "").replaceAll(":", "");
					if (word.contains("end") || word.contains("get") || word.contains("text"))
						continue;

					if (isKey == false) {
						String identifier = treeBank(pos);
						if (!identifier.equals("punctuation") && !identifier.equals("pronoun")
								&& !identifier.equals("determiner") && !identifier.equals("conjunction")
								&& !identifier.equals("modal") && !identifier.equals("adverb")
								&& !identifier.equals("to") && !identifier.equals("proposition")
								&& !identifier.equals("punctuation")) {

							if (!(word.contains(",") || word.contains(":") || word.contains("''") || word.contains(".")
									|| word.contains("/") || word.contains("\\") || word.contains("^")
									|| word.contains("#") || word.contains("!") || word.contains("@")
									|| word.contains("$") || word.contains("%") || word.contains("&")
									|| word.contains("*") || word.contains("(") || word.contains(")")
									|| word.contains("-") || word.contains("+") || word.contains("=")
									|| word.contains("[") || word.contains("]") || word.contains("|")
									|| word.contains("?") || word.contains("~") || word.contains("~")
									|| word.contains("_"))) {

								String javaWord = javaBank(word);
								String rbWord = rbBank(word);

								if (word.equals(javaWord) && word.equals(rbWord))
									eachSentence.add(word);
							}
						}
					}
				}
			}
			if (!eachSentence.isEmpty()) {
				String filteredLine = ("" + eachSentence).replace("[", "").replaceAll("]", "");
				return filteredLine;
			}
		}

		return "NULL";
	}

	public static String treeBank(String pos) {

		if (pos.contains("JJ"))
			return "adjective";
		else if (pos.contains("NN"))
			return "noun";
		else if (pos.contains("RB"))
			return "adverb";
		else if (pos.contains("VB"))
			return "verb";
		else if (pos.contains("PRP"))
			return "pronoun";
		else if (pos.contains("C"))
			return "conjunction";
		else if (pos.contains("MD"))
			return "modal";
		else if (pos.contains("IN"))
			return "proposition";
		else if (pos.contains("W"))
			return "determiner";
		else if (pos.contains("DT"))
			return "determiner";
		else if (pos.contains("TO"))
			return "to";
		else if (pos.contains("CD"))
			return "cardinal number";
		else {
			return pos;
		}
	}

	public static String wordBank(String word) {
		if (word.contains("^") || word.contains("|") || word.contains("_") || word == " " || word.contains("/")
				|| word.contains("is") || word.contains("are") || word.contains("\\") || word.contains("$")
				|| word.contains(".") || word.contains("@") || word.contains("="))
			return "punctuation";
		else
			return word;
	}

	public static String javaBank(String word) {
		String word2 = word.toLowerCase();

		if (word2.equals("public") || word2.equals("private") || word2.equals("static") || word2.equals("final")
				|| word2.equals("void") || word2.equals("main") || word2.equals("class") || word2.contains("implement"))
			return "isclass";
		else if (word2.equals("get") || word2.equals("set") || word2.equals("object") || word2.equals("obj")
				|| word2.equals("null") || word2.equals("int") || word2.equals("double") || word2.equals("string")
				|| word2.equals("args") || word2.equals("return") || word2.equals("long"))
			return "istype";
		else if (word2.equals("import") || word2.contains("override"))
			return "iscommand";
		else if (word2.equals("package") || word2.equals("void") || word2.equals("system") || word.equals("out")
				|| word2.equals("println") || word2.equals("org") || word2.equals("test") || word2.contains("<")
				|| word2.contains(">") || word2.equals("void") || word2.equals("void") || word2.equals("step")
				|| word2.equals("try") || word2.equals("catch") || word2.contains("instance") || word2.equals("com")
				|| word2.equals("springframework") || word2.equals("package") || word2.equals("spring")
				|| word2.equals("hash") || word2.equals("p") || word2.equals("ul") || word2.equals("junit")
				|| word2.contains("efinition") || word2.contains("abstract") || word2.contains("exception")
				|| word2.equals("context") || word2.contains("configuration") || word2.equals("case")
				|| word2.equals("switch") || word2.equals("xml") || word2.equals("return") || word2.equals("comment")
				|| word2.equals("param") || word2.contains("throw") || word2.equals("out") || word2.equals("https")
				|| word2.equals("html") || word2.equals("serializer") || word2.equals("equals")
				|| word2.equals("relationship") || word2.equals("contains") || word2.equals("rel")
				|| word2.equals("equals") || word2.equals("to") || word2.equals("java") || word2.equals("util")
				|| word2.equals("directory") || word2.equals("path") || word2.equals("j") || word2.equals("new")
				|| word2.contains("read") || word2.contains("write") || word2.contains("id") || word2.contains("run")
				|| word2.contains("runner") || word2.contains("application") || word2.contains("write")
				|| word2.contains("date") || word2.contains("true") || word2.contains("false") || word2.contains("map")
				|| word2.contains("handler") || word2.contains("method") || word2.contains("lang")
				|| word2.contains("write") || word2.contains("apache") || word2.contains("time")
				|| word2.contains("write") || word2.contains("token") || word2.contains("buffer")
				|| word2.contains("type") || word2.contains("super") || word2.contains("append")
				|| word2.contains("break") || word2.contains("continue") || word2.contains("and")
				|| word2.contains("result") || word2.contains("boolean") || word2.contains("for")
				|| word2.contains("if") || word2.contains("ignore") || word2.contains("scan") || word2.contains("error")
				|| word2.contains("stream") || word2.contains("id") || word2.contains("object") || word2.contains("put")
				|| word2.contains("add") || word2.contains("remove") || word2.contains("name")
				|| word2.contains("given") || word2.contains("then") || word2.contains("have")
				|| word2.contains("scenario") || word2.contains("there") || word2.contains("assert"))
			return "ismisc";
		return word;
	}

	public static String rbBank(String word) {
		String word2 = word;
		if (word2.contains("encoding") || word2.contains("line") || word2.contains("file") || word2.contains("end")
				|| word2.contains("of") || word2.contains("alias") || word2.contains("and") || word2.contains("begin")
				|| word2.contains("break") || word2.contains("continue") || word2.contains("case")
				|| word2.contains("class") || word2.contains("def") || word2.contains("defined") || word2.contains("do")
				|| word2.contains("else") || word2.contains("elsif") || word2.contains("end")
				|| word2.contains("ensure") || word2.contains("false") || word2.contains("for") || word2.contains("if")
				|| word2.contains("in") || word2.contains("module") || word2.contains("next") || word2.contains("nil")
				|| word2.contains("not") || word2.contains("or") || word2.contains("redo") || word2.contains("rescue")
				|| word2.contains("retry") || word2.contains("return") || word2.contains("self")
				|| word2.contains("super") || word2.contains("then") || word2.contains("true")
				|| word2.contains("undef") || word2.contains("unless") || word2.contains("until")
				|| word2.contains("when") || word2.contains("while") || word2.contains("yield") || word2.contains("%")
				|| word2.contains(">") || word2.contains("<") || word2.contains("=") || word2.contains("#")
				|| word2.contains("null") || word2.contains("require") || word2.contains("set")
				|| word2.contains("assert") || word2.contains("end") || word2.contains("create")
				|| word2.contains("context") || word2.contains("process"))
			return "isRubyKeyWord";
		else {
			return word;
		}
	}

}