package com.fluxo.fluxoapp.users.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    public static final String DEFAULT_TIME_ZONE = "America/Sao_Paulo";

    public enum Status { ACTIVE, DISABLED }

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    /**
     * Nulo até o usuário confirmar a tela "Como você quer ser chamado?".
     * O app usa a ausência deste campo para decidir se mostra essa tela, então o backend NÃO preenche sozinho.
     */
    @Column(name = "preferred_name", length = 50)
    private String preferredName;

    @Column(name = "time_zone", nullable = false, length = 50)
    private String timeZone = DEFAULT_TIME_ZONE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    @Column(name = "terms_accepted_version", nullable = false, length = 20)
    private String termsAcceptedVersion;

    @Column(name = "terms_accepted_at", nullable = false)
    private Instant termsAcceptedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public User(String fullName, String email, String passwordHash, String termsAcceptedVersion) {
        this.fullName = fullName.trim();
        this.email = normalizeEmail(email);
        this.passwordHash = passwordHash;
        this.termsAcceptedVersion = termsAcceptedVersion;
        this.termsAcceptedAt = Instant.now();
    }

    public static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public void changePreferredName(String preferredName) {
        this.preferredName = preferredName.trim();
    }

    public void changePasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
