package edu.cnm.deepdive.codebreaker.model.entity;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;

//tell hybernate it is an entity
@SuppressWarnings("JpaDataSourceORMInspection") //not bother looking for datasource for annotations
@Entity
@Table(name = "user_profile",
    indexes = {
        @Index(columnList = "created") //field names here, not column names
    })
public class User {

  @Id //marks as primary key field
  @GeneratedValue //autogenerates value for us, can only go on a primary key.
  @Column(name = "user_id", updatable = false, columnDefinition = "UUID")
  //if we ever use hybernate to try to update this field, gives an error, tell hybernamet what datatype it is
  private UUID id;

  @Column(updatable = false, nullable = false, unique = true, columnDefinition = "UUID")
  //automatically generates lower snake case field (note: room doesn't do this, hybernate does)
  private UUID externalKey = UUID.randomUUID(); //default value

  @CreationTimestamp
  @Temporal(value = TemporalType.TIMESTAMP) //corresponds to sql type of timestamp
  @Column(nullable = false, updatable = false)
  private Date created;

  @Column(nullable = false, updatable = false, unique = true, length = 30)
  private String oauthKey; //OAuth

  @Column(nullable = false, updatable = true, unique = true, length = 100)
  private String displayName;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
      cascade = CascadeType.ALL, orphanRemoval = true) //mappedBy = specify the field where this relationship was defined (the field where defined the foreign key and ...)
  @OrderBy("created DESC") //when you do a query, here is the order we would like you to use.
  private final List<Game> games = new LinkedList<>(); //when retrieve a user, looking up a list of games by that user

  public UUID getId() {
    return id;
  }

  public UUID getExternalKey() {
    return externalKey;
  }

  public Date getCreated() {
    return created;
  }

  public String getOauthKey() {
    return oauthKey;
  }

  public void setOauthKey(String oauthKey) {
    this.oauthKey = oauthKey;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public List<Game> getGames() {
    return games;
  }
}
