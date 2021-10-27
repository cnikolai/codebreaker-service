package edu.cnm.deepdive.codebreaker.model.entity;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Game {

  private UUID id;

  private UUID externalKey;

  private User user; //thing that we look up with the foreign key, not foreggn key itself

  private Date created;

  private String pool;

  private int length;

  private String text;

}
