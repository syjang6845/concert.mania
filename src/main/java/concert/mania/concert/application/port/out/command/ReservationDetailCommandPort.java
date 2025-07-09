package concert.mania.concert.application.port.out.command;

import concert.mania.concert.domain.model.ReservationDetail;

import java.math.BigDecimal;
import java.util.List;

/**
 * 예매 상세 명령(Command) 작업을 위한 포트 인터페이스
 */
public interface ReservationDetailCommandPort {
    
    /**
     * 예매 상세 정보 저장
     * 
     * @param reservationDetail 저장할 예매 상세 정보
     * @return 저장된 예매 상세 정보
     */
    ReservationDetail save(ReservationDetail reservationDetail);
    
    /**
     * 예매 상세 정보 일괄 저장
     * 
     * @param reservationDetails 저장할 예매 상세 정보 목록
     * @return 저장된 예매 상세 정보 목록
     */
    List<ReservationDetail> saveAll(List<ReservationDetail> reservationDetails);
    
    /**
     * 예매 상세 정보 삭제
     * 
     * @param reservationDetailId 삭제할 예매 상세 정보 ID
     */
    void delete(Long reservationDetailId);
    
    /**
     * 예매 ID로 모든 예매 상세 정보 삭제
     * 
     * @param reservationId 예매 ID
     * @return 삭제된 레코드 수
     */
    int deleteByReservationId(Long reservationId);
    
    /**
     * 예매 상세 정보 가격 업데이트
     * 
     * @param reservationDetailId 예매 상세 정보 ID
     * @param price 변경할 가격
     * @return 업데이트된 예매 상세 정보
     */
    ReservationDetail updatePrice(Long reservationDetailId, BigDecimal price);
}