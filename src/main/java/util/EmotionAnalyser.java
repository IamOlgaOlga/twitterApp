package main.java.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by olga on 24.08.15.
 */
public class EmotionAnalyser {

	private static final String
		NEGATIVE_WORDS_FILE_NAME = "negative-words.txt",
		POSITIVE_WORDS_FILE_NAME = "positive-words.txt";

	private final Set<String>
		negativeWordsSet = new HashSet<>(),
		positiveWordsSet = new HashSet<>();

	public EmotionAnalyser() throws FileNotFoundException {
		createWordSet(NEGATIVE_WORDS_FILE_NAME, negativeWordsSet);
		createWordSet(POSITIVE_WORDS_FILE_NAME, positiveWordsSet);
	}

	private void createWordSet(final String fileName, final Set<String> set) throws FileNotFoundException {
		final ClassLoader classLoader = EmotionAnalyser.class.getClassLoader();
		final URL wordsFileURL = classLoader.getResource(fileName);
		if (wordsFileURL != null) {
			final File file = new File(wordsFileURL.getPath());
			try(
				final BufferedReader
					in = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)
			) {
				String line = in.readLine();
				while (line != null && !line.isEmpty()) {
					set.add(line);
					line = in.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else throw new FileNotFoundException("File with swear words isn't found");
	}

	public final boolean isNegativeTweet(final String tweet) {
		boolean isNegative = false;
		final String[] words = tweet.split("[\\s+\\p{Punct}]+");
		int countNegativeWords = 0, countPositiveWords = 0;
		for (int i = 0; i < words.length; i++) {
			if (negativeWordsSet.contains(words[i])) {
				countNegativeWords++;
			} else if (positiveWordsSet.contains(words[i])) {
				countPositiveWords++;
			}
		}

		if (countNegativeWords >= countPositiveWords) {
			isNegative = true;
		}

		return isNegative;
	}

}
