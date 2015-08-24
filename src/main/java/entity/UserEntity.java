package main.java.entity;

import javax.persistence.*;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by olga on 20.08.15.
 */
@Entity
@Table(
	name = "users",
	uniqueConstraints = {
		@UniqueConstraint( columnNames = "id" ),
		@UniqueConstraint( columnNames = "name" )
	}
)
public class UserEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "location", nullable = true)
	private String location;

	@Column(name = "language", nullable = true)
	private String language;

	@Column(name = "description", nullable = true)
	private String description;

	@Column(name = "followers_count", nullable = true)
	private int followersCount;

	public UserEntity(){
	}

	public UserEntity(
		final String name, final String location,
		final String language, final String description,
		final int followersCount
	) {
		this.name = name;
		this.location = location;
		this.language = language;
		this.description = description;
		this.followersCount = followersCount;
	}

	public final int getId() {
		return id;
	}

	public final void setId(final int id) {
		this.id = id;
	}

	public final String getName() {
		return name;
	}

	public final void setName(final String name) {
		this.name = name;
	}

	public final String getLocation() {
		return location;
	}

	public final void setLocation(final String location) {
		this.location = location;
	}

	public final String getLanguage() {
		return language;
	}

	public final void setLanguage(final String language) {
		this.language = language;
	}

	public final String getDescription() {
		return description;
	}

	public final void setDescription(final String description) {
		this.description = description;
	}

	public final int getFollowersCount() {
		return followersCount;
	}

	public final void setFollowersCount(final int followersCount) {
		this.followersCount = followersCount;
	}

}
