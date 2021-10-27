package edu.cnm.deepdive.codebreaker.model.entity;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;

//tell hybernate it is an entity
@Entity
@Table(name = "user_profile",
    indexes = {
        @Index(columnList = "created") //field names here, not column names
    })
public class User {

  @Id //marks as primary key field
  @GeneratedValue //autogenerates field for us, can only go on a primary key.
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

}
