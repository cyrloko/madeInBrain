package io.madeinbrain.service;

import io.madeinbrain.dto.CreateSourceRequest;
import io.madeinbrain.dto.SourceDTO;
import io.madeinbrain.dto.TheoryDTO;
import io.madeinbrain.entity.Source;
import io.madeinbrain.entity.Theory;
import io.madeinbrain.entity.TheoryHistoryEntry;
import io.madeinbrain.entity.User;
import io.madeinbrain.mapper.SourceMapper;
import io.madeinbrain.mapper.TheoryMapper;
import io.madeinbrain.repository.SourceRepository;
import io.madeinbrain.repository.TheoryHistoryRepository;
import io.madeinbrain.repository.TheoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SourceService {

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private TheoryRepository theoryRepository;

    @Autowired
    private TheoryHistoryRepository historyRepository;

    @Autowired
    private ConfidenceScoreCalculatorService scoreCalculatorService;

    public List<SourceDTO> getSourcesByTheoryId(Long theoryId) {
        return sourceRepository.findByTheoryIdOrderByReliabilityDesc(theoryId)
                .stream()
                .map(SourceMapper.INSTANCE::toSourceDTO)
                .collect(Collectors.toList());
    }

    public Optional<SourceDTO> getSourceById(Long id) {
        return sourceRepository.findById(id).map(SourceMapper.INSTANCE::toSourceDTO);
    }

    //TODO: a modifier
    public Optional<SourceDTO> addSourceToTheory(Long theoryId, CreateSourceRequest request, User user) {
        return theoryRepository.findById(theoryId)
                .map(theory -> {
                    double previousScore = theory.getConfidenceScore();

                    Source source = Source.builder()
                            .title(request.getTitle())
                            .type(request.getType())
                            .author(request.getAuthor())
                            .year(request.getYear())
                            .reliability(request.getReliability())
                            .build();

                    source.setUrl(request.getUrl());
                    source.setDoi(request.getDoi());
                    source.setTheory(theory);

                    source = sourceRepository.save(source);

                    // Recalculer le score de confiance
                    double newScore = scoreCalculatorService.calculateConfidenceScore(theory);
                    theory.setConfidenceScore(scoreCalculatorService.calculateConfidenceScore(theory));
                    theory = theoryRepository.save(theory);

                    // Enregistrer l'historique
                    TheoryHistoryEntry historyEntry = TheoryHistoryEntry.builder()
                            .theory(theory)
                            .actionType(TheoryHistoryEntry.ActionType.UPDATED)
                            .description("Théorie mise à jour")
                            .user(user)
                            .previousConfidenceScore(previousScore)
                            .newConfidenceScore(newScore)
                            .build();
                    historyRepository.save(historyEntry);

                    return SourceMapper.INSTANCE.toSourceDTO(source);
                });
    }

    public boolean deleteTheory(Long id) {
        if (theoryRepository.existsById(id)) {
            theoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<TheoryDTO> getTopTheoriesByConfidence(int limit) {
        List<Theory> theories = theoryRepository.findTop10ByOrderByConfidenceScoreDesc();
        return theories.stream()
                .limit(limit)
                .map(theory -> {
                    TheoryDTO dto = TheoryMapper.INSTANCE.toTheoryDTO(theory);
                    dto.setSources(theory.getSources().stream()
                            .map(SourceMapper.INSTANCE::toSourceDTO)
                            .toList());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<TheoryDTO> getTopTheoriesByContributors(int limit) {
        List<Theory> theories = theoryRepository.findTop10ByOrderByContributorsDesc();
        return theories.stream()
                .limit(limit)
                .map(theory -> {
                    TheoryDTO dto = TheoryMapper.INSTANCE.toTheoryDTO(theory);
                    dto.setSources(theory.getSources().stream()
                            .map(SourceMapper.INSTANCE::toSourceDTO)
                            .toList());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public boolean deleteSource(Long id, User user) {
        return false;
    }

    public List<SourceDTO> getSourcesByType(Source.SourceType type) {
        return null;
    }

    public List<SourceDTO> getHighReliabilitySources(double minReliability) {
        return null;
    }
}