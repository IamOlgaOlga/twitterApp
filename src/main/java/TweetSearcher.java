package main.java;

import main.java.entity.TagEntity;
import main.java.entity.TweetEntity;
import main.java.entity.UserEntity;
import main.java.util.EmotionAnaliser;
import main.java.util.Persister;
import twitter4j.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by olga on 19.08.15.
 */
public final class TweetSearcher
implements Runnable{

	private final Persister daoEntity;
	private final Twitter twitter;
	private final BlockingQueue<String> queueTags;

	private static final int SEARCH_COUNT = 20;

	public TweetSearcher(
		final Persister daoEntity, final Twitter twitter,
		final BlockingQueue<String> queueTags
	){
		this.twitter = twitter;
		this.daoEntity = daoEntity;
		this.queueTags = queueTags;
	}

	@Override
	public final void run() {
		EmotionAnaliser emotionAnaliser;
		String tag;
		Query query;
		QueryResult result;
		List<Status> tweets;

		UserEntity userEntity;
		TweetEntity tweetEntity;
		TagEntity tagEntity;

		for(;;){
			try {
				emotionAnaliser = new EmotionAnaliser();
				tag = queueTags.take();
				if (tag.equals(Producer.SHUTDOWN)) {
					return;
				} else {
					query = new Query(tag);
					query.setCount(SEARCH_COUNT);
					result = twitter.search(query);
					tweets = result.getTweets();
					for (Status tweet : tweets) {

						userEntity = daoEntity.getUser(tweet.getUser().getName());
						if (userEntity == null) {
							userEntity = new UserEntity(
								tweet.getUser().getName(),
								tweet.getUser().getLocation(),
								tweet.getUser().getLang(),
								tweet.getUser().getDescription(),
								tweet.getUser().getFollowersCount()
							);
							daoEntity.saveUser(userEntity);
						}

						tweetEntity = daoEntity.getTweet(userEntity, tweet.getCreatedAt());
						if (tweetEntity == null) {
							tweetEntity = new TweetEntity(
								tweet.getText(),
								userEntity,
								tweet.getCreatedAt(),
								tweet.getRetweetCount(),
								emotionAnaliser.isBadTweet(tweet.getText())
							);
						}

						tagEntity = daoEntity.getTag(tag);
						if (tagEntity == null) {
							tagEntity = new TagEntity(tag);
							tagEntity.getTweets().add(tweetEntity);
							tweetEntity.getTags().add(tagEntity);
							daoEntity.saveTag(tagEntity);
						} else {
							tagEntity.getTweets().add(tweetEntity);
							tweetEntity.getTags().add(tagEntity);
							daoEntity.updateTag(tagEntity);
						}
					}
				}
			} catch (InterruptedException | TwitterException | FileNotFoundException e) {
				e.printStackTrace();
			}

		}
	}

}
