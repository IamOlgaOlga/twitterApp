package main.java;

import main.java.util.Persister;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by olga on 20.08.15.
 */
public class Runner {

	private static final int QUEUE_SIZE = 50;
	private static final String
		TWITTER_KEY = "lBmbfWbhsNQqOobmRtLaoPQdR",
		TWITTER_SECRET = "gjw4Rb1jVZWY4b3ug9WQsDfOP7QsKaJX14oJOHSFnqgHUrf3ag",
		TWITTER_ACCESS_TOKEN = "3428618205-FpCm2uln3KyKAFBA1XIcHh6oOoAKZtknpYvGfQh",
		TWITTER_ACCESS_TOKEN_SECRET = "A6gn54fmIirlqoRlq3RqAfPXh1C1WsyZkz1RdcOyENt1C";

	public static void main(String[] args) {
		//Queue for tags
		final BlockingQueue<String> queueTags = new ArrayBlockingQueue<String>(QUEUE_SIZE);
		//Create DAO
		final Persister persister = new Persister();
		//Create thread for search
		final Thread searcher =  createSearcher(persister, queueTags);
		//Create producer
		final Thread producer = createProducer(persister, queueTags);

		//STarting threads
		producer.start();
		searcher.start();

		//Wait producer
		try {
			producer.join();
			searcher.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//exit
		if (!producer.isInterrupted()) {
			producer.interrupt();
		}
		if (!searcher.isInterrupted()) {
			searcher.interrupt();
		}
		//close hibernate session's factory
		persister.shutdown();
	}

	private static Thread createProducer(final Persister persister, final BlockingQueue<String> queueTags) {
		final Scanner scannerConsole = new Scanner(System.in);
		return new Thread(new Producer(persister, queueTags, scannerConsole));
	}

	private static Thread createSearcher(final Persister persister, final BlockingQueue<String> queueTags) {
		//Connect to twitter
		final ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
			.setOAuthConsumerKey(TWITTER_KEY)
			.setOAuthConsumerSecret(TWITTER_SECRET)
			.setOAuthAccessToken(TWITTER_ACCESS_TOKEN)
			.setOAuthAccessTokenSecret(TWITTER_ACCESS_TOKEN_SECRET);
		final TwitterFactory tf = new TwitterFactory(cb.build());
		final Twitter twitter = tf.getInstance();

		return new Thread(new TweetSearcher(persister, twitter, queueTags));
	}
}
