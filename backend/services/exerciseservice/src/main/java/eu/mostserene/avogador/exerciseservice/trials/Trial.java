package eu.mostserene.avogador.exerciseservice.trials;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "Trials")
public class Trial {
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

    public UUID getId() {
        return id;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean visible) {
        isVisible = visible;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public ProgrammingLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ProgrammingLanguage language) {
        this.language = language;
    }

    public Date getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
    }
}
