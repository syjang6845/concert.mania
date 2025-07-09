package concert.mania.concert.infrastructure.persistence.jpa.adapter.command;

import concert.mania.concert.application.port.out.command.NotificationCommandPort;
import concert.mania.concert.domain.model.Notification;
import concert.mania.concert.domain.model.type.NotificationStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.NotificationJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaNotificationRepository;
import concert.mania.concert.infrastructure.persistence.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 알림 명령 영속성 어댑터
 * 알림 관련 명령 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional
public class NotificationCommandAdapter implements NotificationCommandPort {

    private final DataJpaNotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public Notification save(Notification notification) {
        NotificationJpaEntity entity = notificationMapper.toEntity(notification);
        NotificationJpaEntity savedEntity = notificationRepository.save(entity);
        return notificationMapper.toDomain(savedEntity);
    }

    @Override
    public List<Notification> saveAll(List<Notification> notifications) {
        List<NotificationJpaEntity> entities = notifications.stream()
                .map(notificationMapper::toEntity)
                .collect(Collectors.toList());
        List<NotificationJpaEntity> savedEntities = notificationRepository.saveAll(entities);
        return savedEntities.stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Override
    public int deleteSentNotificationsBefore(LocalDateTime dateTime) {
        return notificationRepository.deleteSentNotificationsBefore(dateTime);
    }

    @Override
    public Notification updateStatus(Long notificationId, NotificationStatus status) {
        NotificationJpaEntity entity = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NoSuchElementException("알림 정보를 찾을 수 없습니다. ID: " + notificationId));

        // Update status and sentAt if needed
        NotificationJpaEntity.NotificationJpaEntityBuilder builder = NotificationJpaEntity.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .type(entity.getType())
                .title(entity.getTitle())
                .content(entity.getContent())
                .status(status)
                .sentAt(entity.getSentAt());

        // Set sentAt if status is SENT
        if (status == NotificationStatus.SENT) {
            builder.sentAt(LocalDateTime.now());
        }

        NotificationJpaEntity updatedEntity = builder.build();
        NotificationJpaEntity savedEntity = notificationRepository.save(updatedEntity);
        return notificationMapper.toDomain(savedEntity);
    }

    @Override
    public Notification markAsSent(Long notificationId) {
        return updateStatus(notificationId, NotificationStatus.SENT);
    }

    @Override
    public Notification markAsFailed(Long notificationId) {
        return updateStatus(notificationId, NotificationStatus.FAILED);
    }

    @Override
    public Notification resetForRetry(Long notificationId) {
        NotificationJpaEntity entity = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NoSuchElementException("알림 정보를 찾을 수 없습니다. ID: " + notificationId));

        if (entity.getStatus() != NotificationStatus.FAILED) {
            throw new IllegalStateException("실패한 알림만 재발송을 위해 초기화할 수 있습니다. ID: " + notificationId);
        }

        return updateStatus(notificationId, NotificationStatus.PENDING);
    }

    @Override
    public Notification createEmailNotification(Long userId, String title, String content) {
        NotificationJpaEntity entity = NotificationJpaEntity.builder()
                .userId(userId)
                .type("EMAIL")
                .title(title)
                .content(content)
                .status(NotificationStatus.PENDING)
                .build();
        NotificationJpaEntity savedEntity = notificationRepository.save(entity);
        return notificationMapper.toDomain(savedEntity);
    }

    @Override
    public Notification createSmsNotification(Long userId, String title, String content) {
        NotificationJpaEntity entity = NotificationJpaEntity.builder()
                .userId(userId)
                .type("SMS")
                .title(title)
                .content(content)
                .status(NotificationStatus.PENDING)
                .build();
        NotificationJpaEntity savedEntity = notificationRepository.save(entity);
        return notificationMapper.toDomain(savedEntity);
    }

    @Override
    public Notification update(Notification notification) {
        // Check if the notification exists
        notificationRepository.findById(notification.getId())
                .orElseThrow(() -> new NoSuchElementException("알림 정보를 찾을 수 없습니다. ID: " + notification.getId()));

        NotificationJpaEntity entity = notificationMapper.toEntity(notification);
        NotificationJpaEntity savedEntity = notificationRepository.save(entity);
        return notificationMapper.toDomain(savedEntity);
    }
}