package main.java;

import main.java.util.Common;
import main.java.util.Persister;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

/**
 * Created by olga on 22.08.15.
 */
public class Producer
implements Runnable {

	private static final String
		EXIT_INDICATOR = "exit",
		STATISTICS_INDICATOR = "get statistics";

	private final Persister persister;
	private final BlockingQueue<String> queueTags;
	private final Scanner scanner;


	public Producer(final Persister persister, final BlockingQueue<String> queueTags, final Scanner scanner) {
		this.persister = persister;
		this.queueTags = queueTags;
		this.scanner = scanner;
	}

	@Override
	public final void run() {
		System.out.println("Put in tags for search");
		System.out.println("Put in <<" + EXIT_INDICATOR + ">> for exit");
		System.out.println("Put in <<" + STATISTICS_INDICATOR + ">> for output statistics about tags");

		while (true){
			final String inputCommand = scanner.nextLine().toLowerCase();
			switch (inputCommand) {
				case (EXIT_INDICATOR):
					try {
						queueTags.put(Common.SHUTDOWN);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return;
				case (STATISTICS_INDICATOR):
					List<String> tagsList = persister.getLastWeekTag();
					System.out.print("Tags in the last week: ");
					for (final String tag: tagsList) {
						System.out.print(tag + "; ");
					}
					System.out.println();
					//
					tagsList = persister.getTheMostPopularTag();
					System.out.print("The most popular tags in the last week: ");
					for (final String tag: tagsList) {
						System.out.print(tag + "; ");
					}
					System.out.println();
					break;
				default:
					final String[] tags = inputCommand.split("[\\s+\\p{Punct}]+");
					for (String tag: tags) {
						try {
							queueTags.put(tag);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					break;
			}
		}
	}
}
