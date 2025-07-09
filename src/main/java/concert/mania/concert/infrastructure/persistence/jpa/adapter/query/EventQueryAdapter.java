package concert.mania.concert.infrastructure.persistence.jpa.adapter.query;

import concert.mania.concert.application.port.out.query.EventQueryPort;
import concert.mania.concert.domain.model.Event;
import concert.mania.concert.domain.model.type.EventStatus;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaEventRepository;
import concert.mania.concert.infrastructure.persistence.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 이벤트 조회 영속성 어댑터
 * 이벤트 관련 조회 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventQueryAdapter implements EventQueryPort {

    private final DataJpaEventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id)
                .map(eventMapper::toDomain);
    }

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll().stream()
                .map(eventMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByEventType(String eventType) {
        return eventRepository.findByEventType(eventType).stream()
                .map(eventMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByStatus(EventStatus status) {
        return eventRepository.findByStatus(status).stream()
                .map(eventMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByEventTypeAndStatus(String eventType, EventStatus status) {
        return eventRepository.findByEventTypeAndStatus(eventType, status).stream()
                .map(eventMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByCreatedAtAfter(LocalDateTime createdAt) {
        return eventRepository.findByCreatedAtAfter(createdAt).stream()
                .map(eventMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByProcessedAtAfter(LocalDateTime processedAt) {
        return eventRepository.findByProcessedAtAfter(processedAt).stream()
                .map(eventMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return eventRepository.findByCreatedAtBetween(startTime, endTime).stream()
                .map(eventMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findPendingEvents() {
        return eventRepository.findPendingEvents().stream()
                .map(eventMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findFailedEvents() {
        return eventRepository.findFailedEvents().stream()
                .map(eventMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countPendingEventsByType(String eventType) {
        return eventRepository.countPendingEventsByType(eventType);
    }

    @Override
    public long countFailedEventsByType(String eventType) {
        return eventRepository.countFailedEventsByType(eventType);
    }
}