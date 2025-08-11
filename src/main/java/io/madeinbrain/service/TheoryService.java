package io.madeinbrain.service;

import io.madeinbrain.dto.*;
import io.madeinbrain.entity.*;
import io.madeinbrain.mapper.SourceMapper;
import io.madeinbrain.mapper.TheoryMapper;
import io.madeinbrain.repository.*;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TheoryService {

    private TheoryRepository theoryRepository;

    private SourceRepository sourceRepository;

    private TheoryHistoryRepository historyRepository;

    private TheoryMapper theoryMapper;

    private SourceMapper sourceMapper;

    private ConfidenceScoreCalculatorService scoreCalculatorService;

    public Page<TheoryDTO> getAllTheories(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return theoryRepository.findAll(pageable)
                .map(theory ->
                        TheoryDTO.builder()
                                .sources(theory.getSources().stream()
                                    .map(SourceMapper.INSTANCE::toSourceDTO)
                                    .toList())
                                .build());
    }

    public Page<TheoryDTO> searchTheories(String searchTerm, Theory.Category category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("confidenceScore").descending());

        Page<Theory> theories;
        if (category != null && searchTerm != null && !searchTerm.trim().isEmpty()) {
            theories = theoryRepository.findByCategoryAndSearchTerm(category, searchTerm.trim(), pageable);
        } else if (category != null) {
            theories = theoryRepository.findByCategory(category, pageable);
        } else if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            theories = theoryRepository.findByTitleOrDescriptionContaining(searchTerm.trim(), pageable);
        } else {
            theories = theoryRepository.findAll(pageable);
        }

        return theories.map(theory -> {
            TheoryDTO dto = TheoryMapper.INSTANCE.toTheoryDTO(theory);
            dto.setSources(theory.getSources().stream()
                    .map(SourceMapper.INSTANCE::toSourceDTO)
                    .toList());
            return dto;
        });
    }

    public Optional<TheoryDTO> getTheoryById(Long id) {
        return theoryRepository.findById(id)
                .map(theory -> {
                    TheoryDTO dto = TheoryMapper.INSTANCE.toTheoryDTO(theory);
                    dto.setSources(theory.getSources().stream()
                            .map(SourceMapper.INSTANCE::toSourceDTO)
                            .toList());
                    return dto;
                });
    }

    public TheoryDTO createTheory(CreateTheoryRequest request, User user) {

        Theory theory = Theory.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .supportingEvidence(request.getSupportingEvidence())
                .opposingEvidence(request.getOpposingEvidence())
                .contributors(1)
                .build();

        // Sauvegarder la théorie d'abord
        theory = theoryRepository.save(theory);

        // Ajouter les sources
        if (request.getSources() != null) {
            for (CreateSourceRequest sourceRequest : request.getSources()) {
                Source source = Source.builder()
                        .title(sourceRequest.getTitle())
                        .type(sourceRequest.getType())
                        .author(sourceRequest.getAuthor())
                        .year(sourceRequest.getYear())
                        .reliability(sourceRequest.getReliability())
                        .url(sourceRequest.getUrl())
                        .doi(sourceRequest.getDoi())
                        .theory(theory)
                        .build();

                sourceRepository.save(source);
                theory.getSources().add(source);
            }
        }

        // Calculer le score de confiance
        double confidenceScore = scoreCalculatorService.calculateConfidenceScore(theory);
        theory.setConfidenceScore(confidenceScore);
        theory = theoryRepository.save(theory);

        // Enregistrer l'historique
        TheoryHistoryEntry historyEntry =  TheoryHistoryEntry.builder()
                .theory(theory)
                .user(user)
                .actionType(TheoryHistoryEntry.ActionType.CREATED)
                .description("Théorie créée")
                .previousConfidenceScore(0.0)
                .newConfidenceScore(confidenceScore)
                .build();
        historyRepository.save(historyEntry);

        TheoryDTO dto = TheoryMapper.INSTANCE.toTheoryDTO(theory);
        dto.setSources(theory.getSources().stream()
                .map(SourceMapper.INSTANCE::toSourceDTO)
                .toList());
        return dto;
    }

    public Optional<TheoryDTO> updateTheory(Long id, CreateTheoryRequest request, User user, Source source) {
        return theoryRepository.findById(id)
                .flatMap(theory -> {
                    double previousScore = theory.getConfidenceScore();
                    theory.setTitle(request.getTitle());
                    theory.setDescription(request.getDescription());
                    theory.setCategory(request.getCategory());
                    theory.setSupportingEvidence(request.getSupportingEvidence());
                    theory.setOpposingEvidence(request.getOpposingEvidence());

                    // Recalculer le score de confiance
                    double newScore = scoreCalculatorService.calculateConfidenceScore(theory);
                    theory.setConfidenceScore(newScore);
                    theoryRepository.save(theory);

                    // Enregistrer l'historique
                    TheoryHistoryEntry historyEntry = TheoryHistoryEntry.builder()
                            .theory(theory)
                            .user(user)
                            .actionType(TheoryHistoryEntry.ActionType.SOURCE_ADDED)
                            .description("Source ajoutée: " + source.getTitle())
                            .previousConfidenceScore(previousScore)
                            .newConfidenceScore(newScore)
                            .build();
                    historyRepository.save(historyEntry);

                    return Optional.ofNullable(TheoryMapper.INSTANCE.toTheoryDTO(theory));
                });
    }

    public boolean deleteSource(Long id, User user) {
        Optional<Source> sourceOpt = sourceRepository.findById(id);
        if (sourceOpt.isPresent()) {
            Source source = sourceOpt.get();
            Theory theory = source.getTheory();
            double previousScore = theory.getConfidenceScore();

            sourceRepository.delete(source);

            // Recalculer le score de confiance
            double newScore = scoreCalculatorService.calculateConfidenceScore(theory);
            theory.setConfidenceScore(newScore);
            theoryRepository.save(theory);

            // Enregistrer l'historique
            TheoryHistoryEntry historyEntry = TheoryHistoryEntry.builder()
                    .theory(theory)
                    .user(user)
                    .actionType(TheoryHistoryEntry.ActionType.SOURCE_REMOVED)
                    .description("Source ajoutée: " + source.getTitle())
                    .previousConfidenceScore(previousScore)
                    .newConfidenceScore(newScore)
                    .build();
            historyRepository.save(historyEntry);

            return true;
        }
        return false;
    }

    public List<SourceDTO> getSourcesByType(Source.SourceType type) {
        return sourceRepository.findByType(type)
                .stream()
                .map(SourceMapper.INSTANCE::toSourceDTO)
                .toList();
    }

    public List<SourceDTO> getHighReliabilitySources(double minReliability) {
        return sourceRepository.findByMinimumReliability(minReliability)
                .stream()
                .map(SourceMapper.INSTANCE::toSourceDTO)
                .toList();
    }


    public boolean deleteTheory(Long id) {
        //TODO: à implementer
        return false;
    }
}