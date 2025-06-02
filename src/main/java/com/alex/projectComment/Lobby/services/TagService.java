package com.alex.projectComment.Lobby.services;

import com.alex.projectComment.Lobby.dtos.TagDTO;
import com.alex.projectComment.Lobby.entities.Tag;
import com.alex.projectComment.Lobby.mappers.TagMapper;
import com.alex.projectComment.Lobby.repositories.TagRepository;
import com.alex.projectComment.infra.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagService {
  @Autowired
  private TagRepository tagRepository;
  @Autowired
  private TagMapper tagMapper;

  @Transactional(readOnly = true)
  public List<TagDTO> findAll() {
    List<Tag> result = tagRepository.findAll();
    return tagMapper.listEntityToListDTO(result);
  }

  @Transactional(readOnly = true)
  public TagDTO findByName(String name) {
    Tag result = tagRepository.findByNameLikeIgnoreCase(name).orElseThrow(() -> new EntityNotFoundException("Não existe uma tag com esse nome: " + name));
    return tagMapper.toDTO(result);
  }

  @Transactional(readOnly = true)
  public boolean existsByName(String name) {
    return tagRepository.existsByNameIgnoreCase(name);
  }

  @Transactional
  public TagDTO createTag(String tagName) {
    if (tagRepository.existsByNameIgnoreCase(tagName)) {
      throw new IllegalArgumentException("Tag com o nome de: " + tagName + " já existe.");
    }

    Tag newTag = new Tag();
    newTag.setName(tagName);

    newTag = tagRepository.save(newTag);

    return tagMapper.toDTO(newTag);
  }
}
