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
		Session session = HibernateUtil.getSessionFactory().openSession();
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
		Session session = HibernateUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(UserEntity.class)
			.add(Restrictions.eq("name", name));
		final List<UserEntity> result = criteria.list();
		session.close();
		if (result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}

	public TweetEntity getTweet(final UserEntity userEntity, final Date timestamp){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(TweetEntity.class)
			.add(Restrictions.eq("userEntity", userEntity))
			.add(Restrictions.eq("timestamp", timestamp));
		final List<TweetEntity> result = criteria.list();
		session.close();
		if (result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}

	public TagEntity getTag(final String word){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(TagEntity.class)
			.add(Restrictions.eq("word", word));
		final List<TagEntity> result = criteria.list();
		session.close();
		if (result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}

	public List<String> getTheMostPopularTag(){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createSQLQuery(
			"select word from tags,tweets,tags_tweets " +
				"where (tags_tweets.id_tag=tags.id and tags_tweets.id_tweet=tweets.id " +
				"and tweets.timestamp > now() - interval '1 week') " +
				"group by tags.word having count(tags_tweets.id_tag) >= all (" +
				"select count(tags_tweets.id_tag) from tags_tweets group by tags_tweets.id_tag);"
		);
		final List<String> result = query.list();
		session.close();
		if (result.isEmpty()) {
			return null;
		} else {
			return result;
		}
	}

	public List<String> getLastWeekTag(){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createSQLQuery(
			"select word from tags,tweets,tags_tweets " +
				"where (tags_tweets.id_tag=tags.id and tags_tweets.id_twit=tweets.id " +
				"and tweets.timestamp > now() - interval '1 week') " +
				"group by tags.word;"
		);
		final List<String> result = query.list();
		session.close();
		if (result.isEmpty()) {
			return null;
		} else {
			return result;
		}
	}
}
