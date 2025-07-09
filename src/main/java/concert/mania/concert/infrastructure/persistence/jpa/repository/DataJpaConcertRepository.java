package concert.mania.concert.infrastructure.persistence.jpa.repository;

import concert.mania.concert.infrastructure.persistence.jpa.entity.ConcertJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.querydsl.ConcertCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 콘서트 JPA 엔티티에 대한 Spring Data JPA 리포지토리
 */
public interface DataJpaConcertRepository extends JpaRepository<ConcertJpaEntity, Long>, QueryByExampleExecutor<ConcertJpaEntity>, ConcertCustomRepository {



    /**
     * 예매 오픈 일시가 특정 날짜 이후인 콘서트 목록 조회
     * 
     * @param dateTime 기준 날짜시간
     * @return 예매 오픈 예정인 콘서트 목록
     */
    List<ConcertJpaEntity> findByReservationOpenDateTimeAfter(LocalDateTime dateTime);
    
    /**
     * 특정 기간에 진행되는 콘서트 목록 조회
     * 
     * @param startDateTime 시작 날짜시간
     * @param endDateTime 종료 날짜시간
     * @return 해당 기간에 진행되는 콘서트 목록
     */
    @Query("SELECT c FROM ConcertJpaEntity c WHERE c.startDateTime <= :endDateTime AND c.endDateTime >= :startDateTime")
    List<ConcertJpaEntity> findConcertsByPeriod(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);
    
    /**
     * 제목에 특정 키워드가 포함된 콘서트 목록 조회
     * 
     * @param keyword 검색 키워드
     * @return 검색 결과 콘서트 목록
     */
    List<ConcertJpaEntity> findByTitleContaining(String keyword);
    
    /**
     * 특정 장소에서 진행되는 콘서트 목록 조회
     * 
     * @param venue 장소명
     * @return 해당 장소의 콘서트 목록
     */
    List<ConcertJpaEntity> findByVenue(String venue);
}