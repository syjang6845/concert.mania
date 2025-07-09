package concert.mania.concert.infrastructure.persistence.jpa.adapter.command;

import concert.mania.concert.application.port.out.command.EventCommandPort;
import concert.mania.concert.domain.model.Event;
import concert.mania.concert.domain.model.type.EventStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.EventJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaEventRepository;
import concert.mania.concert.infrastructure.persistence.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 이벤트 명령 영속성 어댑터
 * 이벤트 관련 명령 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional
public class EventCommandAdapter implements EventCommandPort {

    private final DataJpaEventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public Event save(Event event) {
        EventJpaEntity entity = eventMapper.toEntity(event);
        EventJpaEntity savedEntity = eventRepository.save(entity);
        return eventMapper.toDomain(savedEntity);
    }

    @Override
    public List<Event> saveAll(List<Event> events) {
        List<EventJpaEntity> entities = events.stream()
                .map(eventMapper::toEntity)
                .collect(Collectors.toList());
        List<EventJpaEntity> savedEntities = eventRepository.saveAll(entities);
        return savedEntities.stream()
                .map(eventMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    @Override
    public int deleteProcessedEventsBefore(LocalDateTime dateTime) {
        return eventRepository.deleteProcessedEventsBefore(dateTime);
    }

    @Override
    public Event updateStatus(Long eventId, EventStatus status) {
        EventJpaEntity entity = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("이벤트 정보를 찾을 수 없습니다. ID: " + eventId));

        // Update status and processedAt if needed
        EventJpaEntity.EventJpaEntityBuilder builder = EventJpaEntity.builder()
                .id(entity.getId())
                .eventType(entity.getEventType())
                .payload(entity.getPayload())
                .status(status)
                .processedAt(entity.getProcessedAt());

        // Set processedAt if status is PROCESSED
        if (status == EventStatus.PROCESSED) {
            builder.processedAt(LocalDateTime.now());
        }

        EventJpaEntity updatedEntity = builder.build();
        EventJpaEntity savedEntity = eventRepository.save(updatedEntity);
        return eventMapper.toDomain(savedEntity);
    }

    @Override
    public Event markAsProcessed(Long eventId) {
        return updateStatus(eventId, EventStatus.PROCESSED);
    }

    @Override
    public Event markAsFailed(Long eventId) {
        return updateStatus(eventId, EventStatus.FAILED);
    }

    @Override
    public Event resetForRetry(Long eventId) {
        EventJpaEntity entity = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("이벤트 정보를 찾을 수 없습니다. ID: " + eventId));

        if (entity.getStatus() != EventStatus.FAILED) {
            throw new IllegalStateException("실패한 이벤트만 재처리를 위해 초기화할 수 있습니다. ID: " + eventId);
        }

        return updateStatus(eventId, EventStatus.PENDING);
    }

    @Override
    public Event publishEvent(String eventType, String payload) {
        EventJpaEntity entity = EventJpaEntity.builder()
                .eventType(eventType)
                .payload(payload)
                .status(EventStatus.PENDING)
                .build();
        EventJpaEntity savedEntity = eventRepository.save(entity);
        return eventMapper.toDomain(savedEntity);
    }

    @Override
    public Event update(Event event) {
        // Check if the event exists
        eventRepository.findById(event.getId())
                .orElseThrow(() -> new NoSuchElementException("이벤트 정보를 찾을 수 없습니다. ID: " + event.getId()));

        EventJpaEntity entity = eventMapper.toEntity(event);
        EventJpaEntity savedEntity = eventRepository.save(entity);
        return eventMapper.toDomain(savedEntity);
    }
}