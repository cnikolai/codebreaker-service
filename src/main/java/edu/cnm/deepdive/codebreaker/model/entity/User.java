package edu.cnm.deepdive.codebreaker.model.entity;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;

//tell hybernate it is an entity
@Entity
public class User {

  @Id //marks as primary key field
  @GeneratedValue //autogenerates field for us, can only go on a primary key.
  @Column(name = "user_id", updatable = false, columnDefinition = "UUID") //if we ever use hybernate to try to update this field, gives an error, tell hybernamet what datatype it is
  private UUID id;

  @Column(updatable = false, nullable = false, unique = true, columnDefinition = "UUID")  //automatically generates lower snake case field (note: room doesn't do this, hybernate does)
  private UUID externalKey = UUID.randomUUID(); //default value

  @CreationTimestamp
  @Temporal(value = TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date created;



}
