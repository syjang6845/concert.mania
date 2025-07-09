package concert.mania.concert.infrastructure.persistence.jpa.adapter.command;

import concert.mania.concert.application.port.out.command.ReservationDetailCommandPort;
import concert.mania.concert.domain.model.ReservationDetail;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ReservationDetailJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaReservationDetailRepository;
import concert.mania.concert.infrastructure.persistence.mapper.ReservationDetailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 예매 상세 명령 영속성 어댑터
 * 예매 상세 관련 명령 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional
public class ReservationDetailCommandAdapter implements ReservationDetailCommandPort {

    private final DataJpaReservationDetailRepository reservationDetailRepository;
    private final ReservationDetailMapper reservationDetailMapper;

    @Override
    public ReservationDetail save(ReservationDetail reservationDetail) {
        ReservationDetailJpaEntity entity = reservationDetailMapper.toEntity(reservationDetail);
        ReservationDetailJpaEntity savedEntity = reservationDetailRepository.save(entity);
        return reservationDetailMapper.toDomain(savedEntity);
    }

    @Override
    public List<ReservationDetail> saveAll(List<ReservationDetail> reservationDetails) {
        List<ReservationDetailJpaEntity> entities = reservationDetails.stream()
                .map(reservationDetailMapper::toEntity)
                .collect(Collectors.toList());
        List<ReservationDetailJpaEntity> savedEntities = reservationDetailRepository.saveAll(entities);
        return savedEntities.stream()
                .map(reservationDetailMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long reservationDetailId) {
        reservationDetailRepository.deleteById(reservationDetailId);
    }

    @Override
    public int deleteByReservationId(Long reservationId) {
        // Spring Data JPA에서는 삭제된 레코드 수를 반환하지 않으므로,
        // 먼저 해당 예매 ID의 상세 정보 수를 조회한 후 삭제
        long count = reservationDetailRepository.countByReservationId(reservationId);
        
        // JPQL을 사용한 벌크 삭제 쿼리 실행
        // 참고: 실제 구현에서는 @Query 어노테이션을 사용한 메서드를 리포지토리에 추가해야 함
        // @Query("DELETE FROM ReservationDetailJpaEntity rd WHERE rd.reservation.id = :reservationId")
        // int deleteByReservationId(@Param("reservationId") Long reservationId);
        
        // 현재는 findByReservationId로 조회 후 하나씩 삭제하는 방식으로 구현
        List<ReservationDetailJpaEntity> entities = reservationDetailRepository.findByReservationId(reservationId);
        reservationDetailRepository.deleteAll(entities);
        
        return (int) count;
    }

    @Override
    public ReservationDetail updatePrice(Long reservationDetailId, BigDecimal price) {
        ReservationDetailJpaEntity entity = reservationDetailRepository.findById(reservationDetailId)
                .orElseThrow(() -> new IllegalArgumentException("예매 상세 정보를 찾을 수 없습니다: " + reservationDetailId));

        // 새로운 엔티티 생성 (Builder 패턴 사용)
        ReservationDetailJpaEntity updatedEntity = ReservationDetailJpaEntity.builder()
                .id(entity.getId())
                .reservation(entity.getReservation())
                .seat(entity.getSeat())
                .price(price)
                .build();

        ReservationDetailJpaEntity savedEntity = reservationDetailRepository.save(updatedEntity);
        return reservationDetailMapper.toDomain(savedEntity);
    }
}