package concert.mania.concert.infrastructure.persistence.jpa.entity;

import concert.mania.common.converter.BooleanToYNConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "concerts", indexes = {
    @Index(name = "idx_concert_reservation_open", columnList = "reservationOpenDateTime"),
    @Index(name = "idx_concert_is_active", columnList = "isActive")
})
public class ConcertJpaEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_id")
    private Long id; // 콘서트 고유 식별자
    
    @Column(nullable = false, length = 255)
    private String title; // 콘서트 제목
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; // 콘서트 상세 설명
    
    @Column(nullable = false)
    private LocalDateTime startDateTime; // 콘서트 시작 일시
    
    @Column(nullable = false)
    private LocalDateTime endDateTime; // 콘서트 종료 일시
    
    @Column(nullable = false, length = 255)
    private String venue; // 콘서트 개최 장소명
    
    @Column(nullable = false, length = 255)
    private String venueAddress; // 콘서트 개최 장소 주소
    
    @Column(nullable = false)
    private LocalDateTime reservationOpenDateTime; // 예매 오픈 일시
    
    @Column(nullable = false)
    private LocalDateTime reservationCloseDateTime; // 예매 마감 일시

    @Column(name ="is_active" ,nullable = false, length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Convert(converter = BooleanToYNConverter.class)
    private boolean active; // 콘서트 활성화 상태 ('Y', 'N')
    
    // 양방향 관계 설정
    @OneToMany(mappedBy = "concert", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<SeatGradeJpaEntity> seatGrades = new ArrayList<>(); // 좌석 등급 목록
    
    @OneToMany(mappedBy = "concert", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<SeatJpaEntity> seats = new ArrayList<>(); // 좌석 목록
}