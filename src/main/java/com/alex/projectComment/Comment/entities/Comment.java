package com.alex.projectComment.Comment.entities;

import com.alex.projectComment.Lobby.entities.Domain;
import com.alex.projectComment.Section.entities.Section;
import com.alex.projectComment.User.entities.User;
import com.alex.projectComment.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_comment")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String content;

  @ManyToOne
  private User user;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
  private boolean isEdited;

  @OneToMany
  private List<Comment> responsesComments;

  @ManyToOne
  private Section section;

  @ManyToOne
  private Domain domain;

  @Enumerated(EnumType.STRING)
  private StatusEnum status;

  private Integer likesCount;
  private Integer dislikesCount;

  @OneToMany
  private List<Likes> likes;

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    Comment comment = (Comment) o;
    return getId() != null && Objects.equals(getId(), comment.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }
}
