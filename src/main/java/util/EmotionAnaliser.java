package main.java.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;

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
public class EmotionAnaliser {

	private final String swearWordsFileName = "swearWords.csv";

	private final Set<String> swearWordsSet = new HashSet<>();

	public EmotionAnaliser() throws FileNotFoundException {
		final ClassLoader classloader = EmotionAnaliser.class.getClassLoader();
		final URL swearWordsFileURL = classloader.getResource(swearWordsFileName);
		if (swearWordsFileURL != null) {
			final File swearWords = new File(swearWordsFileURL.getPath());


			try(
				final BufferedReader
					in = Files.newBufferedReader(swearWords.toPath(), StandardCharsets.UTF_8)
			) {
				final Iterable<CSVRecord> recIter = CSVFormat.RFC4180.parse(in);
				for(final CSVRecord nextRec : recIter) {
					for (int i = 0; i < nextRec.size(); i++){
						swearWordsSet.add(nextRec.get(i));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else throw new FileNotFoundException("File with swear words isn't found");
	}

	public final boolean isBadTweet(final String tweet) {
		final String[] words = tweet.split("[\\s+\\p{Punct}]+");
		int countBadWords = 0;
		for (int i = 0; i < words.length; i++) {
			if (swearWordsSet.contains(words[i])) {
				countBadWords++;
			}
		}

		final double frequencyOfBadWords = countBadWords * 1.0 / words.length;

		if (countBadWords > 0 && frequencyOfBadWords > 0.3) {
			return true;
		}

		return false;
	}

}
