package concert.mania.concert.infrastructure.persistence.jpa.entity;

import concert.mania.concert.domain.model.type.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "seats")
public class SeatJpaEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id; // 좌석 고유 식별자
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private ConcertJpaEntity concert; // 연관된 콘서트
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_grade_id", nullable = false)
    private SeatGradeJpaEntity seatGrade; // 좌석 등급
    
    @Column(nullable = false, length = 20)
    private String seatNumber; // 좌석 번호 (예: "A-12")
    
    @Column(nullable = false)
    private Integer seatRow; // 좌석 행 번호
    
    @Column(nullable = false)
    private Integer seatCol; // 좌석 열 번호
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatStatus status; // 좌석 상태 (AVAILABLE, SELECTED, RESERVED, SOLD)
}