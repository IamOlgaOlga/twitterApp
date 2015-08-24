package main.java.entity;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by olga on 22.08.15.
 */
@Entity
@Table(
	name = "tags",
	uniqueConstraints = {
		@UniqueConstraint( columnNames = "id" ),
		@UniqueConstraint( columnNames = "word" )
	}
)
public class TagEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;

	@Column(name = "word", nullable = false)
	private String word;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "tags_tweets", joinColumns = {
		@JoinColumn(name = "id_tag", nullable = false, updatable = false) },
		inverseJoinColumns = { @JoinColumn(name = "id_tweet",
			nullable = false, updatable = false) })
	private Set<TweetEntity> tweets;

	public TagEntity(){}

	public TagEntity(final String word){
		this.word = word;
		this.setTweets(new HashSet<TweetEntity>());
	}

	public final int getId() {
		return id;
	}

	public final void setId(final int id) {
		this.id = id;
	}

	public final String getWord() {
		return word;
	}

	public final void setWord(final String word) {
		this.word = word;
	}

	public final Set<TweetEntity> getTweets() {
		return tweets;
	}

	public final void setTweets(final Set<TweetEntity> tweets) {
		this.tweets = tweets;
	}
}
