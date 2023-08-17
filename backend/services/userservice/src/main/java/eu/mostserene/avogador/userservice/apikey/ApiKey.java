package eu.mostserene.avogador.userservice.apikey;

import eu.mostserene.avogador.userservice.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "ApiKey",
        uniqueConstraints = @UniqueConstraint(columnNames={"user_id", "name"})
)
public class ApiKey {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private String name;

    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private User user;

    @NotNull
    private String keyHash;

    @NotNull
    private Timestamp creationTimestamp = Timestamp.from(Instant.now());

    @NotNull
    private Timestamp expirationTimestamp;

    public ApiKey() {
    }

    public ApiKey(User user, String name, String keyHash, Timestamp expirationTimestamp) {
        this.name = name;
        this.user = user;
        this.keyHash = keyHash;
        this.expirationTimestamp = expirationTimestamp;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getKeyHash() {
        return keyHash;
    }

    public void setKeyHash(String keyHash) {
        this.keyHash = keyHash;
    }

    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public Timestamp getExpirationTimestamp() {
        return expirationTimestamp;
    }
}
