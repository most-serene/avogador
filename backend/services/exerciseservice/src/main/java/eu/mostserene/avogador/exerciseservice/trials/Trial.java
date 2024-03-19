package eu.mostserene.avogador.exerciseservice.trials;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

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

    @NotNull
    private UUID courseId;

    @NotNull
    private String name;

    @NotNull
    private Boolean isVisible;

    @NotNull
    private Boolean isPublic;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProgrammingLanguage language;

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

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIsVisible(Boolean visible) {
        isVisible = visible;
    }

    public void setIsPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public void setLanguage(ProgrammingLanguage language) {
        this.language = language;
    }

    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
    }
}
