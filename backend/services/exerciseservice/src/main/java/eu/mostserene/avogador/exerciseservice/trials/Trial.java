package eu.mostserene.avogador.exerciseservice.trials;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Entity
@Table(name = "Trials")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Trial {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @NotNull
    private UUID courseId;

    @Setter
    @NotNull
    private String name;

    @Setter
    @NotNull
    private Boolean isVisible;

    @Setter
    @NotNull
    private Boolean isPublic;

    @Setter
    @NotNull
    @Enumerated(EnumType.STRING)
    private ProgrammingLanguage language;

    @Setter
    private Date startTimestamp;

    public Trial() {
    }

    public Trial(UUID courseId, String name, Boolean isVisible, Boolean isPublic, ProgrammingLanguage language) {
        this.courseId = courseId;
        this.name = name;
        this.isVisible = isVisible;
        this.isPublic = isPublic;
        this.language = language;
    }

    public Trial(UUID courseId, String name, Boolean isVisible, Boolean isPublic, ProgrammingLanguage language, Date startTimestamp) {
        this(courseId, name, isVisible, isPublic, language);
        this.startTimestamp = startTimestamp;
    }

    public abstract TrialType getTrialType();

    public abstract boolean isAfterDeadline(Date deadline);
}
