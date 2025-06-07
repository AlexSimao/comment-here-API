package com.alex.projectComment.Section.controllers;

import com.alex.projectComment.Section.dtos.SectionDTO;
import com.alex.projectComment.Section.dtos.SectionRequestDTO;
import com.alex.projectComment.Section.dtos.SectionUpdateRequestDTO;
import com.alex.projectComment.Section.services.SectionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sections")
public class SectionController {
  @Autowired
  private SectionService sectionService;

  @GetMapping
  public ResponseEntity<Page<SectionDTO>> findAll(Pageable pageable) {
    Page<SectionDTO> result = sectionService.findAll(pageable);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}")
  public ResponseEntity<SectionDTO> findById(@PathVariable Long id) {
    SectionDTO result = sectionService.findById(id);
    return ResponseEntity.ok(result);
  }

  @PostMapping
  public ResponseEntity<SectionDTO> createSection(@RequestParam Long lobbyId, @RequestBody SectionRequestDTO sectionRequest, HttpServletRequest request) {
    SectionDTO result = sectionService.createSection(lobbyId, sectionRequest, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @PutMapping("/{id}")
  public ResponseEntity<SectionDTO> updateSection(@PathVariable Long id, @RequestBody SectionUpdateRequestDTO sectionUpdateRequestDTO, HttpServletRequest request) {
    SectionDTO result = sectionService.updateSection(id, sectionUpdateRequestDTO, request);
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSection(@PathVariable Long id, HttpServletRequest request) {
    sectionService.deleteSection(id, request);
    return ResponseEntity.noContent().build();
  }
}
