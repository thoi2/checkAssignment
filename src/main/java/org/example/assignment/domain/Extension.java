package org.example.assignment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "extension",
        uniqueConstraints = @UniqueConstraint(name = "uk_extension_name", columnNames = "name"),
        indexes = {
                @Index(name = "idx_extension_fixed", columnList = "is_fixed")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Extension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    // UI에서 기본 제공되는 확장자 여부 false면 커스텀 확장자.
    @Column(name = "is_fixed", nullable = false)
    private boolean fixed;

    @Column(name = "is_blocked", nullable = false)
    private boolean blocked;

    // 동시 토글/수정 시 낙관락 적용.
    @Version
    private Long version;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    //소문자 통일
    public Extension(String name, boolean fixed, boolean blocked) {
        this.name = normalize(name);
        this.fixed = fixed;
        this.blocked = blocked;
    }

    public void toggleBlocked() {
        this.blocked = !this.blocked;
    }

    public boolean isDeletable() {
        return !this.fixed;
    }

    private static String normalize(String name) {
        return name.trim().toLowerCase();
    }
}
