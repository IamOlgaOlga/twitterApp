package main.java.util;

import main.java.entity.TagEntity;
import main.java.entity.TweetEntity;
import main.java.entity.UserEntity;

import org.hibernate.*;

import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.List;

/**
 * Created by olga on 20.08.15.
 */
public final class Persister {

	private static final String
		SQL_QUERY_TAGS = "select word from tags,tweets,tags_tweets " +
		"where (tags_tweets.id_tag=tags.id and tags_tweets.id_tweet=tweets.id " +
		"and tweets.timestamp > now() - interval '1 week') group by tags.word;",
		SQL_QUERY_POPULAR_TAG = "select word from tags,tweets,tags_tweets " +
			"where (tags_tweets.id_tag=tags.id and tags_tweets.id_tweet=tweets.id " +
			"and tweets.timestamp > now() - interval '1 week') " +
			"group by tags.word having count(tags_tweets.id_tag) >= all " +
			"(select count(tags_tweets.id_tag) from tags_tweets,tweets  " +
			"where (tags_tweets.id_tweet=tweets.id and tweets.timestamp > now() - interval '1 week') " +
			"group by tags_tweets.id_tag);",
		NAME = "name",
		USER_ENTITY = "userEntity",
		TIMESTAMP = "timestamp",
		WORD = "word";

	public final void saveUser(final UserEntity userEntity){
		final Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			session.save(userEntity);
			session.getTransaction().commit();
		} catch (final HibernateException e) {
			session.getTransaction().rollback();
		} finally {
			session.close();
		}
	}

	public final void saveTweet(final TweetEntity tweetEntity){
		final Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			session.save(tweetEntity);
			session.getTransaction().commit();
		} catch (final HibernateException e) {
			session.getTransaction().rollback();
		} finally {
			session.close();
		}
	}

	public final void saveTag(final TagEntity tagEntity){
		final Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			session.save(tagEntity);
			session.getTransaction().commit();
		} catch (final HibernateException e) {
			session.getTransaction().rollback();
		} finally {
			session.close();
		}
	}

	public void updateTag(final TagEntity tagEntity){
		final Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			session.update(tagEntity);
			session.getTransaction().commit();
		} catch (final HibernateException e) {
			session.getTransaction().rollback();
		} finally {
			session.close();
		}
	}

	public void shutdown(){
		HibernateUtil.shutdown();
	}

	public UserEntity getUser(final String name){
		UserEntity userEntity;
		final Session session = HibernateUtil.getSessionFactory().openSession();
		final Criteria criteria = session.createCriteria(UserEntity.class)
			.add(Restrictions.eq(NAME, name));
		final List<UserEntity> result = criteria.list();
		session.close();
		if (result.isEmpty()) {
			userEntity = null;
		} else {
			userEntity = result.get(0);
		}

		return userEntity;
	}

	public TweetEntity getTweet(final UserEntity userEntity, final Date timestamp){
		TweetEntity tweetEntity;
		final Session session = HibernateUtil.getSessionFactory().openSession();
		final Criteria criteria = session.createCriteria(TweetEntity.class)
			.add(Restrictions.eq(USER_ENTITY, userEntity))
			.add(Restrictions.eq(TIMESTAMP, timestamp));
		final List<TweetEntity> result = criteria.list();
		session.close();
		if (result.isEmpty()) {
			tweetEntity = null;
		} else {
			tweetEntity = result.get(0);
		}

		return tweetEntity;
	}

	public TagEntity getTag(final String word){
		TagEntity tagEntity;
		final Session session = HibernateUtil.getSessionFactory().openSession();
		final Criteria criteria = session.createCriteria(TagEntity.class)
			.add(Restrictions.eq(WORD, word));
		final List<TagEntity> result = criteria.list();
		session.close();
		if (result.isEmpty()) {
			tagEntity = null;
		} else {
			tagEntity = result.get(0);
		}

		return tagEntity;
	}

	public List<String> getTheMostPopularTag(){
		final Session session = HibernateUtil.getSessionFactory().openSession();
		final Query query = session.createSQLQuery(SQL_QUERY_POPULAR_TAG);
		List<String> result = query.list();
		session.close();
		if (result.isEmpty()) {
			result = null;
		}

		return result;
	}

	public List<String> getLastWeekTag(){
		final Session session = HibernateUtil.getSessionFactory().openSession();
		final Query query = session.createSQLQuery(SQL_QUERY_TAGS);
		List<String> result = query.list();
		session.close();
		if (result.isEmpty()) {
			result = null;
		}

		return result;
	}
}
