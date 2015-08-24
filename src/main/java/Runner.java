package main.java;

import main.java.util.Persister;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by olga on 17.08.15.
 */
public class Runner {

	public static void main(String[] args) {
		//Constants
		final int QUEUE_SIZE = 50;
		final long TIMEOUT_MILLISEC = 1800000; //30 min
		//Queue for tags
		final BlockingQueue<String> queueTags = new ArrayBlockingQueue<String>(QUEUE_SIZE);

		//Connect to twitter
		final ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
			.setOAuthConsumerKey("lBmbfWbhsNQqOobmRtLaoPQdR")
			.setOAuthConsumerSecret("gjw4Rb1jVZWY4b3ug9WQsDfOP7QsKaJX14oJOHSFnqgHUrf3ag")
			.setOAuthAccessToken("3428618205-FpCm2uln3KyKAFBA1XIcHh6oOoAKZtknpYvGfQh")
			.setOAuthAccessTokenSecret("A6gn54fmIirlqoRlq3RqAfPXh1C1WsyZkz1RdcOyENt1C");
		final TwitterFactory tf = new TwitterFactory(cb.build());
		final Twitter twitter = tf.getInstance();
		//Create DAO
		final Persister daoEntity = new Persister();
		//Create thread for search
		final Thread searcher =  new Thread(new TweetSearcher(daoEntity, twitter, queueTags));
		//Create producer
		final Scanner scannerConsole = new Scanner(System.in);
		final Thread producer = new Thread(new Producer(daoEntity, queueTags, scannerConsole));

		//STarting threads
		producer.start();
		searcher.start();

		//Wait producer
		try {
			producer.join(TIMEOUT_MILLISEC);
			searcher.join(TIMEOUT_MILLISEC);
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
		daoEntity.shutdown();
	}
}
