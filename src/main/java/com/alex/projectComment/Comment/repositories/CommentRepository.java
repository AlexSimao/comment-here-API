package com.alex.projectComment.Comment.repositories;

import com.alex.projectComment.Comment.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
  Page<Comment> findAllBySectionId(Long sectionId, Pageable pageable);
}
