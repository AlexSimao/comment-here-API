package com.alex.projectComment.Lobby.entities;

import com.alex.projectComment.Lobby.enums.VisibilityEnum;
import com.alex.projectComment.User.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_lobby")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Lobby {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private LocalDateTime creationDate;
  private List<String> tags;
  private List<String> domains;

  @Enumerated(EnumType.STRING)
  private VisibilityEnum visibility;

  @ManyToOne
  private User creator;
  @ManyToOne
  private User userPrime;

  @ManyToMany
  @JoinTable(name = "user_admin_lobby", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "lobby_id"))
  private List<User> usersAdmin;

}
