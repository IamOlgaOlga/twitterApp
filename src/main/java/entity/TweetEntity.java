package main.java.entity;

import javax.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by olga on 20.08.15.
 */
@Entity
@Table(
	name = "tweets",
	uniqueConstraints = {
		@UniqueConstraint( columnNames = "id" ),
		@UniqueConstraint( columnNames = {"user_id", "timestamp"} )
	}
)
public class TweetEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;

	@Column(name = "text", nullable = false)
	private String text;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity userEntity;

	@Column(name = "timestamp", nullable = false)
	private Date timestamp;

	@Column(name = "retwits", nullable = true)
	private int retwits;

	@Column(name = "bad_emotion", nullable = true)
	private boolean badEmotion;


	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "tweets")
	private Set<TagEntity> tags;

	public TweetEntity(){
	}

	public TweetEntity(
		final String text, final UserEntity userEntity,
		final Date timestamp, final int retwits, final boolean badEmotion
	){
		this.text = text;
		this.userEntity = userEntity;
		this.timestamp = timestamp;
		this.retwits = retwits;
		this.badEmotion = badEmotion;
		this.setTags(new HashSet<TagEntity>());
	}

	public final int getId() {
		return id;
	}

	public final void setId(final int id) {
		this.id = id;
	}

	public final String getText() {
		return text;
	}

	public final void setText(final String text) {
		this.text = text;
	}

	public final UserEntity getUserEntity() {
		return userEntity;
	}

	public final void setUserEntity(final UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	public final Date getTimestamp() {
		return timestamp;
	}

	public final void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public final int getRetwits() {
		return retwits;
	}

	public final void setRetwits(final int retwits) {
		this.retwits = retwits;
	}

	public final Set<TagEntity> getTags() {
		return tags;
	}

	public final void setTags(final Set<TagEntity> tags) {
		this.tags = tags;
	}

	public final boolean isBadEmotion() {
		return badEmotion;
	}

	public final void setBadEmotion(final boolean badEmotion) {
		this.badEmotion = badEmotion;
	}
}
