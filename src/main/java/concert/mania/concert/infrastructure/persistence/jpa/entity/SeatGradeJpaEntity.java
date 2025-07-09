package concert.mania.concert.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "seat_grades")
public class SeatGradeJpaEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_grade_id")
    private Long id; // 좌석 등급 고유 식별자
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private ConcertJpaEntity concert; // 연관된 콘서트
    
    @Column(nullable = false, length = 50)
    private String name; // 좌석 등급명 (VIP, R, S, A 등)
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // 좌석 등급별 가격
    
    @Column(columnDefinition = "TEXT")
    private String description; // 좌석 등급 설명
    
    @Column(nullable = false)
    private Integer capacity;// 해당 등급의 총 좌석 수용 인원

    
    // 양방향 관계 설정
    @OneToMany(mappedBy = "seatGrade", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SeatJpaEntity> seats = new ArrayList<>(); // 해당 등급에 속한 좌석 목록
}