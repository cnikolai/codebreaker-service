package edu.cnm.deepdive.codebreaker.model.entity;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;

@SuppressWarnings("JpaDataSourceORMInspection")//not try to find a data source and inspect for id.
@Entity
@Table(
    indexes = {
        @Index(columnList = "game_id, created") //more general to more specific
    }
)
public class Guess {

  @Id //mark field as primary key
  @GeneratedValue
  @Column(name = "guess_id", updatable = false)
  private UUID id;

  @Column(nullable = false, updatable = false, unique = true)
  private UUID externalKey = UUID.randomUUID();

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date created;

  @Column(name = "guess_text", nullable = false, updatable = false, length = 20)
  private String text;

  @Column(nullable = false,updatable = false)
  private int exactMatches;

  @Column(nullable = false,updatable = false)
  private int nearMatches;

  @ManyToOne(fetch = FetchType.EAGER, optional = false) //optional => many side of this is mandatroy
  @JoinColumn(name = "game_id", nullable = false, updatable = false)//almost every many to one annotation will have a join column annotation as well
  private Game game; //note: a game object can't be stored in a table, but can be looked up by a join

  public UUID getId() {
    return id;
  }

  public UUID getExternalKey() {
    return externalKey;
  }

  public Date getCreated() {
    return created;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public int getExactMatches() {
    return exactMatches;
  }

  public void setExactMatches(int exactMatches) {
    this.exactMatches = exactMatches;
  }

  public int getNearMatches() {
    return nearMatches;
  }

  public void setNearMatches(int nearMatches) {
    this.nearMatches = nearMatches;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }
}
