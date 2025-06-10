package com.alex.projectComment.Comment.controllers;

import com.alex.projectComment.Comment.dtos.CommentMinDTO;
import com.alex.projectComment.Comment.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("comments")
public class CommentController {
  @Autowired
  private CommentService commentService;

  @GetMapping("/section/{sectionId}")
  public Page<CommentMinDTO> findAllCommentBySectionId(@PathVariable Long sectionId, Pageable pageable) {
    return commentService.findAllCommentBySectionId(sectionId, pageable);
  }

}
