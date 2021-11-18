package edu.cnm.deepdive.codebreaker.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
@JsonInclude(Include.NON_NULL)//if any of the values are not null, include them
@JsonPropertyOrder(value = {"id", "created", "displayName"})//the order of the items returned - don't have to do this in capstone.
public class User {

  @Id //marks as primary key field
  @GeneratedValue //autogenerates value for us, can only go on a primary key.
  @Column(name = "user_id", updatable = false, columnDefinition = "UUID")
  //if we ever use hybernate to try to update this field, gives an error, tell hybernamet what datatype it is
  @JsonIgnore//Jackson api (faster xml)
  private UUID id;

  @Column(updatable = false, nullable = false, unique = true, columnDefinition = "UUID")
  //automatically generates lower snake case field (note: room doesn't do this, hybernate does)
  @JsonProperty(value = "id", access = Access.READ_ONLY)
  private UUID externalKey = UUID.randomUUID(); //default value

  @CreationTimestamp
  @Temporal(value = TemporalType.TIMESTAMP) //corresponds to sql type of timestamp
  @Column(nullable = false, updatable = false)
  private Date created;

  @Column(nullable = false, updatable = false, unique = true, length = 30)
  @JsonIgnore
  private String oauthKey; //OAuth

  @Column(nullable = false, updatable = true, unique = true, length = 100)
  private String displayName;

  @OneToMany(mappedBy = "user", fetch = FetchType.EAGER,
      cascade = CascadeType.ALL, orphanRemoval = true) //mappedBy = specify the field where this relationship was defined (the field where defined the foreign key and ...)
  @OrderBy("created DESC") //when you do a query, here is the order we would like you to use.
  @JsonIgnore
  private final List<Game> games = new LinkedList<>(); //when retrieve a user, looking up a list of games by that user

  /**
   * Returns the primary key identifier for this instance.
   * @return
   */
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

  /**
   * Assigns a unique value to the oauthkey field.  This will be obtained provided by the user when this instance is created.
   * @param oauthKey
   */
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
