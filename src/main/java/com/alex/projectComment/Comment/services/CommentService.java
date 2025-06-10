package com.alex.projectComment.Comment.services;

import com.alex.projectComment.Comment.dtos.CommentMinDTO;
import com.alex.projectComment.Comment.entities.Comment;
import com.alex.projectComment.Comment.mapper.CommentMapper;
import com.alex.projectComment.Comment.repositories.CommentRepository;
import com.alex.projectComment.Section.services.SectionService;
import com.alex.projectComment.enums.StatusEnum;
import com.alex.projectComment.enums.VisibilityEnum;
import com.alex.projectComment.infra.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
  @Autowired
  private CommentMapper commentMapper;
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private LikeService likeService;
  @Autowired
  private SectionService sectionService;

  @Transactional(readOnly = true)
  public Page<CommentMinDTO> findAllCommentBySectionId(Long sectionId, Pageable pageable) {
    if (sectionId == null || sectionId <= 0) {
      throw new IllegalArgumentException("Invalid section ID");
    }

    if (!sectionService.existsById(sectionId, StatusEnum.DELETED)) {
      throw new EntityNotFoundException("Section not found with ID: " + sectionId);
    }

    if (sectionService.findById(sectionId).getLobby().getVisibility() == VisibilityEnum.PRIVATE) {
      throw new IllegalArgumentException("Section com ID " + sectionId + " é privado e não pode ser acessado.");
    }

    Page<CommentMinDTO> result = commentRepository.findAllBySectionId(sectionId, pageable)
        .map(commentMapper::toMinDto);

    result.forEach(comment -> {
      comment.setLikesCount(likeService.getLikesCount(comment.getId()));
      comment.setDislikesCount(likeService.getDislikesCount(comment.getId()));
    });

    return result;
  }

  @Transactional(readOnly = true)
  public boolean existsById(Long id, StatusEnum status) {
    return commentRepository.existsByIdAndStatus(id, status);
  }

  @Transactional(readOnly = true)
  public CommentMinDTO findById(Long id) {
    Comment result = commentRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Comentário com Id: " + id + " não encontrado."));

    return commentMapper.toMinDto(result);
  }

}
