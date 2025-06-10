package com.alex.projectComment.Comment.controllers;

import com.alex.projectComment.Comment.dtos.CommentMinDTO;
import com.alex.projectComment.Comment.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<Page<CommentMinDTO>> findAllCommentBySectionId(@PathVariable Long sectionId, Pageable pageable) {
    Page<CommentMinDTO> result = commentService.findAllCommentBySectionId(sectionId, pageable);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CommentMinDTO> findById(@PathVariable Long id) {
    CommentMinDTO result = commentService.findById(id);
    return ResponseEntity.ok(result);
  }


}
