package com.alex.projectComment.Comment.services;

import com.alex.projectComment.Comment.dtos.CommentMinDTO;
import com.alex.projectComment.Comment.dtos.CommentRequestDTO;
import com.alex.projectComment.Comment.entities.Comment;
import com.alex.projectComment.Comment.mapper.CommentMapper;
import com.alex.projectComment.Comment.repositories.CommentRepository;
import com.alex.projectComment.Lobby.entities.Domain;
import com.alex.projectComment.Lobby.mappers.DomainMapper;
import com.alex.projectComment.Lobby.services.DomainService;
import com.alex.projectComment.Section.entities.Section;
import com.alex.projectComment.Section.mapper.SectionMapper;
import com.alex.projectComment.Section.services.SectionService;
import com.alex.projectComment.User.entities.User;
import com.alex.projectComment.User.mappers.UserMapper;
import com.alex.projectComment.User.services.UserService;
import com.alex.projectComment.enums.StatusEnum;
import com.alex.projectComment.enums.VisibilityEnum;
import com.alex.projectComment.infra.exceptions.EntityNotFoundException;
import com.alex.projectComment.infra.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
  @Autowired
  private TokenService tokenService;
  @Autowired
  private UserService userService;
  @Autowired
  private UserMapper userMapper;
  @Autowired
  private SectionMapper sectionMapper;
  @Autowired
  private DomainService domainService;
  @Autowired
  private DomainMapper domainMapper;

  @Transactional(readOnly = true)
  public Page<CommentMinDTO> findAllCommentBySectionId(Long sectionId, Pageable pageable) {
    if (sectionId == null || sectionId <= 0) {
      throw new IllegalArgumentException("Invalid section ID");
    }

    if (sectionService.existsByIdAndStatus(sectionId, StatusEnum.DELETED)) {
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

  @Transactional
  public CommentMinDTO createComment(Long sectionId, CommentRequestDTO commentRequestDTO, HttpServletRequest request) {

    if (sectionId == null) {
      throw new IllegalArgumentException("Invalid section ID");
    }

    if (commentRequestDTO == null || commentRequestDTO.content() == null || commentRequestDTO.content().isBlank()) {
      throw new IllegalArgumentException("O conteúdo do comentário não pode ser vazios.");
    }

    if (!sectionService.existsByIdAndStatus(sectionId, StatusEnum.ACTIVE)) {
      throw new EntityNotFoundException("Section not found with ID: " + sectionId);
    }

    if (sectionService.findById(sectionId).getLobby().getVisibility() == VisibilityEnum.PRIVATE) {
      throw new IllegalArgumentException("Section com ID " + sectionId + " é privado e não pode ser acessado.");
    }

    String token = tokenService.recoverToken(request);
    User user = userMapper.toEntity(userService.findById(tokenService.getTokenId(token)));
    Section section = sectionMapper.toEntity(sectionService.findById(sectionId));
    Domain domain = domainService.existsByName(request.getRequestURL().toString())
        ? domainMapper.toEntity(domainService.findByName(request.getRequestURL().toString()))
        : domainMapper.toEntity(domainService.createDomain(request.getRequestURL().toString()));

    System.out.println(domain.getName());

    Comment newComment = new Comment();
    newComment.setContent(commentRequestDTO.content());
    newComment.setCreatedBy(user);
    newComment.setCreatedAt(LocalDateTime.now());
    newComment.setEdited(false);
    newComment.setSection(section);
    newComment.setStatus(StatusEnum.ACTIVE);
    newComment.setDomain(domain);
    newComment.setLikesCount(0);
    newComment.setDislikesCount(0);

    Comment savedComment = commentRepository.save(newComment);
    return commentMapper.toMinDto(savedComment);

  }
}
