package com.alex.projectComment.Comment.services;

import com.alex.projectComment.Comment.repositories.LikesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
  @Autowired
  private LikesRepository likesRepository;

  public Integer getLikesCount(Long commentId) {
    return likesRepository.countByCommentIdAndIsLikedTrue(commentId);
  }

  public Integer getDislikesCount(Long commentId) {
    return likesRepository.countByCommentIdAndIsDislikedTrue(commentId);
  }

}
