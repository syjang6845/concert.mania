package concert.mania.concert.infrastructure.persistence.jpa.adapter.command;

import concert.mania.concert.application.port.out.command.ConcertCommandPort;
import concert.mania.concert.domain.model.Concert;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ConcertJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaConcertRepository;
import concert.mania.concert.infrastructure.persistence.mapper.ConcertMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 콘서트 명령 영속성 어댑터
 * 콘서트 관련 명령 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional
public class ConcertCommandAdapter implements ConcertCommandPort {

    private final DataJpaConcertRepository concertRepository;
    private final ConcertMapper concertMapper;

    @Override
    public Concert save(Concert concert) {
        ConcertJpaEntity entity = concertMapper.toEntity(concert);
        ConcertJpaEntity savedEntity = concertRepository.save(entity);
        return concertMapper.toDomain(savedEntity);
    }

    @Override
    public List<Concert> saveAll(List<Concert> concerts) {
        List<ConcertJpaEntity> entities = concerts.stream()
                .map(concertMapper::toEntity)
                .collect(Collectors.toList());
        List<ConcertJpaEntity> savedEntities = concertRepository.saveAll(entities);
        return savedEntities.stream()
                .map(concertMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long concertId) {
        concertRepository.deleteById(concertId);
    }

    @Override
    public Concert updateActiveStatus(Long concertId, boolean active) {
        ConcertJpaEntity entity = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("콘서트를 찾을 수 없습니다: " + concertId));
        
        // 새로운 엔티티 생성 (Builder 패턴 사용)
        ConcertJpaEntity updatedEntity = ConcertJpaEntity.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .startDateTime(entity.getStartDateTime())
                .endDateTime(entity.getEndDateTime())
                .venue(entity.getVenue())
                .venueAddress(entity.getVenueAddress())
                .reservationOpenDateTime(entity.getReservationOpenDateTime())
                .reservationCloseDateTime(entity.getReservationCloseDateTime())
                .active(active)
                .seatGrades(entity.getSeatGrades())
                .seats(entity.getSeats())
                .build();
        
        ConcertJpaEntity savedEntity = concertRepository.save(updatedEntity);
        return concertMapper.toDomain(savedEntity);
    }

    @Override
    public Concert update(Concert concert) {
        ConcertJpaEntity existingEntity = concertRepository.findById(concert.getId())
                .orElseThrow(() -> new IllegalArgumentException("콘서트를 찾을 수 없습니다: " + concert.getId()));
        
        // 새로운 엔티티 생성 (Builder 패턴 사용)
        ConcertJpaEntity updatedEntity = ConcertJpaEntity.builder()
                .id(existingEntity.getId())
                .title(concert.getTitle())
                .description(concert.getDescription())
                .startDateTime(concert.getStartDateTime())
                .endDateTime(concert.getEndDateTime())
                .venue(concert.getVenue())
                .venueAddress(concert.getVenueAddress())
                .reservationOpenDateTime(concert.getReservationOpenDateTime())
                .reservationCloseDateTime(concert.getReservationCloseDateTime())
                .active(concert.isActive())
                .seatGrades(existingEntity.getSeatGrades()) // 기존 관계 유지
                .seats(existingEntity.getSeats()) // 기존 관계 유지
                .build();
        
        ConcertJpaEntity savedEntity = concertRepository.save(updatedEntity);
        return concertMapper.toDomain(savedEntity);
    }
}