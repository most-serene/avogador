package eu.mostserene.avogador.exerciseservice.practices;

import eu.mostserene.avogador.exerciseservice.trials.ProgrammingLanguage;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import eu.mostserene.avogador.exerciseservice.trials.TrialType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "Practices")
public class Practice extends Trial {

    @NotNull
    private Date deadline;

    public Practice() {
    }

    public Practice(UUID courseId, String name, Boolean isVisible, Boolean isPublic, ProgrammingLanguage language, Date deadline) {
        super(courseId, name, isVisible, isPublic, language);
        this.deadline = deadline;
    }

    public Practice(UUID courseId, String name, Boolean isVisible, Boolean isPublic, ProgrammingLanguage language, Date startTimestamp, Date deadline) {
        super(courseId, name, isVisible, isPublic, language, startTimestamp);
        this.deadline = deadline;
    }

    @Override
    public TrialType getTrialType() {
        return TrialType.PRACTICE;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public boolean areTimestampsValid() {
        return getStartTimestamp().after(new Date()) && getDeadline().after(getStartTimestamp());
    }
}
