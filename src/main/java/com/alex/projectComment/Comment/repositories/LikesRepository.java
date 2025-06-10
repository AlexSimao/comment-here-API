package com.alex.projectComment.Comment.repositories;

import com.alex.projectComment.Comment.entities.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
  Integer countByCommentIdAndIsLikedTrue(Long commentId);

  Integer countByCommentIdAndIsDislikedTrue(Long commentId);

}