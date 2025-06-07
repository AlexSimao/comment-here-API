package com.alex.projectComment.Section.entities;

import com.alex.projectComment.Lobby.entities.Lobby;
import com.alex.projectComment.User.entities.User;
import com.alex.projectComment.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_section")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Section {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private LocalDateTime createdAt;

  @ManyToOne
  private User createdBy;

  @ManyToOne
  private Lobby lobby;
  @Enumerated(EnumType.STRING)
  private StatusEnum status;
}
