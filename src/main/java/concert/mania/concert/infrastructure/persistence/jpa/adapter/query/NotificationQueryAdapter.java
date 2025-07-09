package concert.mania.concert.infrastructure.persistence.jpa.adapter.query;

import concert.mania.concert.application.port.out.query.NotificationQueryPort;
import concert.mania.concert.domain.model.Notification;
import concert.mania.concert.domain.model.type.NotificationStatus;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaNotificationRepository;
import concert.mania.concert.infrastructure.persistence.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 알림 조회 영속성 어댑터
 * 알림 관련 조회 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryAdapter implements NotificationQueryPort {

    private final DataJpaNotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationRepository.findById(id)
                .map(notificationMapper::toDomain);
    }

    @Override
    public List<Notification> findAll() {
        return notificationRepository.findAll().stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserId(Long userId) {
        return notificationRepository.findByUserId(userId).stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByType(String type) {
        return notificationRepository.findByType(type).stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status).stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status) {
        return notificationRepository.findByUserIdAndStatus(userId, status).stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByTypeAndStatus(String type, NotificationStatus status) {
        return notificationRepository.findByTypeAndStatus(type, status).stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByCreatedAtAfter(LocalDateTime createdAt) {
        return notificationRepository.findByCreatedAtAfter(createdAt).stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findBySentAtAfter(LocalDateTime sentAt) {
        return notificationRepository.findBySentAtAfter(sentAt).stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return notificationRepository.findByCreatedAtBetween(startTime, endTime).stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findPendingNotifications() {
        return notificationRepository.findPendingNotifications().stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findFailedNotifications() {
        return notificationRepository.findFailedNotifications().stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countPendingNotificationsByUserId(Long userId) {
        return notificationRepository.countPendingNotificationsByUserId(userId);
    }
}