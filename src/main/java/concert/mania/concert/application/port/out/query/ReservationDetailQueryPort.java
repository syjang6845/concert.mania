package concert.mania.concert.application.port.out.query;

import concert.mania.concert.domain.model.ReservationDetail;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 예매 상세 조회(Query) 작업을 위한 포트 인터페이스
 */
public interface ReservationDetailQueryPort {
    
    /**
     * ID로 예매 상세 정보 조회
     * 
     * @param id 예매 상세 정보 ID
     * @return 예매 상세 정보 (Optional)
     */
    Optional<ReservationDetail> findById(Long id);
    
    /**
     * 예매 ID로 예매 상세 정보 목록 조회
     * 
     * @param reservationId 예매 ID
     * @return 예매 상세 정보 목록
     */
    List<ReservationDetail> findByReservationId(Long reservationId);
    
    /**
     * 좌석 ID로 예매 상세 정보 조회
     * 
     * @param seatId 좌석 ID
     * @return 예매 상세 정보 (Optional)
     */
    Optional<ReservationDetail> findBySeatId(Long seatId);
    
    /**
     * 예매 ID로 예매 상세 정보 수 조회
     * 
     * @param reservationId 예매 ID
     * @return 예매 상세 정보 수
     */
    long countByReservationId(Long reservationId);
    
    /**
     * 예매 ID로 총 금액 계산
     * 
     * @param reservationId 예매 ID
     * @return 총 금액
     */
    BigDecimal sumPriceByReservationId(Long reservationId);
    
    /**
     * 좌석 등급 ID로 예매 상세 정보 목록 조회
     * 
     * @param seatGradeId 좌석 등급 ID
     * @return 예매 상세 정보 목록
     */
    List<ReservationDetail> findBySeatGradeId(Long seatGradeId);
    
    /**
     * 콘서트 ID로 예매 상세 정보 목록 조회
     * 
     * @param concertId 콘서트 ID
     * @return 예매 상세 정보 목록
     */
    List<ReservationDetail> findByConcertId(Long concertId);
    
    /**
     * 사용자 ID로 예매 상세 정보 목록 조회
     * 
     * @param userId 사용자 ID
     * @return 예매 상세 정보 목록
     */
    List<ReservationDetail> findByUserId(Long userId);
}