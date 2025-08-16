package io.madeinbrain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.madeinbrain.domain.Revision;
import io.madeinbrain.repository.RevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RevisionService {

    private final RevisionRepository revisionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void record(String entityType, String entityId, String actor, String reason, Map<String, Object> changeSet) {
        try {
            String json = objectMapper.writeValueAsString(changeSet);
            var rev = Revision.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .actor(actor)
                    .reason(reason)
                    .changeSetJson(json)
                    .createdAt(ZonedDateTime.now().toInstant())
                    .build();
            revisionRepository.save(rev);
        } catch (Exception e) {
            // swallow to avoid breaking the main flow; in real life, use proper logging
        }
    }
}
