package main.java;

import main.java.entity.TagEntity;
import main.java.entity.TweetEntity;
import main.java.entity.UserEntity;
import main.java.util.Common;
import main.java.util.EmotionAnalyser;
import main.java.util.Persister;
import twitter4j.*;

import javax.jws.soap.SOAPBinding;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by olga on 22.08.15.
 */
public final class TweetSearcher
implements Runnable{

	private static final int SEARCH_COUNT = 20;

	private final Persister daoEntity;
	private final Twitter twitter;
	private final BlockingQueue<String> queueTags;

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
		while (true) {
			try {
				final EmotionAnalyser emotionAnalyser = new EmotionAnalyser();
				final String tag = queueTags.take();
				if (tag.equals(Common.SHUTDOWN)) {
					return;
				} else {
					final Query query = new Query(tag);
					query.setCount(SEARCH_COUNT);
					final QueryResult result = twitter.search(query);
					final List<Status> tweets = result.getTweets();

					for (Status tweet : tweets) {

						UserEntity userEntity = processUser(tweet);

						TweetEntity tweetEntity = processTweet(tweet, userEntity, emotionAnalyser);

						processTag(tag, tweetEntity);
					}
				}
			} catch (InterruptedException | TwitterException | FileNotFoundException e) {
				e.printStackTrace();
			}

		}
	}

	private UserEntity processUser(final Status tweet){
		UserEntity userEntity = daoEntity.getUser(tweet.getUser().getName());
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
		return userEntity;
	}

	private TweetEntity processTweet(
		final Status tweet, final UserEntity userEntity, final EmotionAnalyser emotionAnalyser
	){
		TweetEntity tweetEntity = daoEntity.getTweet(userEntity, tweet.getCreatedAt());
		if (tweetEntity == null) {
			tweetEntity = new TweetEntity(
				tweet.getText(),
				userEntity,
				tweet.getCreatedAt(),
				tweet.getRetweetCount(),
				emotionAnalyser.isNegativeTweet(tweet.getText())
			);
		}

		return tweetEntity;
	}

	private TagEntity processTag(final String tag, final TweetEntity tweetEntity){
		TagEntity tagEntity = daoEntity.getTag(tag);
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

		return tagEntity;
	}
}
