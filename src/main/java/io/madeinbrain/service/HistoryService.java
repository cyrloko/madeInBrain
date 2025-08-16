package io.madeinbrain.service;

import io.madeinbrain.domain.Theory;
import io.madeinbrain.dto.ScoreDtos;
import io.madeinbrain.repository.ConfidenceSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final ConfidenceSnapshotRepository snapshotRepository;

    public ScoreDtos.HistoryResponse getHistory(Theory theory) {
        var list = snapshotRepository.findByTheoryOrderByCapturedAtAsc(theory).stream()
                .map(s -> new ScoreDtos.SnapshotPoint(s.getCapturedAt(), s.getScore()))
                .collect(Collectors.toList());
        return ScoreDtos.HistoryResponse.builder().history(list).build();
    }
}
