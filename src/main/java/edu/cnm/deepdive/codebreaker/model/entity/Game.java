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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.ManyToAny;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(
    indexes = {
        @Index(columnList = "poolSize"),//field name
        @Index(columnList = "user_id, created")
    }
)
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({"id", "created", "pool", "length", "solved", "text"})
public class Game {

  @Id
  @GeneratedValue //automatically creates this
  @Column(name = "game_id", updatable = false, columnDefinition = "UUID")//there is no UUID type in general in database, but telling hibernate that trust me, there is a UUID type in database
  @JsonIgnore
  private UUID id;

  @Column(nullable = false, updatable = false, columnDefinition = "UUID", unique = true)
  @JsonProperty(value = "id", access = Access.READ_ONLY)
  private UUID externalKey = UUID.randomUUID();

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, updatable = false)
  @JsonIgnore
  private User user; //thing that we look up with the foreign key, not foreggn key itself

  @CreationTimestamp//hibernate timestamp
  @Temporal(TemporalType.TIMESTAMP)//when only attribute that going to specify, don't need value=Temp...
  @Column(nullable = false, updatable = false)
  @JsonProperty(access = Access.READ_ONLY) //if I post a new game with created, don't accept it.
  private Date created;

  @Column(nullable = false, updatable = false, length = 255)
  private String pool;

  @Column(nullable = false, updatable = false)
  @JsonIgnore
  private int poolSize;

  @Column(nullable = false, updatable = false)
  private int length;

  @Column(name = "game_text", nullable = false, updatable = false, length = 20)
  @JsonIgnore
  private String text;

  @OneToMany(mappedBy = "game", fetch = FetchType.EAGER,
      cascade = CascadeType.ALL, orphanRemoval = true)//name of field where this is defined, not game column
  @OrderBy("created ASC")
  @JsonIgnore
  private final List<Guess> guesses = new LinkedList<>();

  public UUID getId() {
    return id;
  }

  public UUID getExternalKey() {
    return externalKey;
  }

  public Date getCreated() {
    return created;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getPool() {
    return pool;
  }

  public void setPool(String pool) {
    this.pool = pool;
  }

  public int getPoolSize() {
    return poolSize;
  }

  public void setPoolSize(int poolSize) {
    this.poolSize = poolSize;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public List<Guess> getGuesses() {
    return guesses;
  }

  public boolean isSolved() {
    return guesses
        .stream()
        .anyMatch((guess) -> guess.getExactMatches() == length);
  }

  @JsonProperty(value = "text")//to outside world, this is called text
  public String getSecretCode() {
    return isSolved() ? text : null;
  }
}
