package eu.mostserene.avogador.exerciseservice.courses;

import lombok.Getter;

@Getter
public enum CourseRole {
    EXTERNAL(-1),
    STUDENT(0),
    COLLABORATOR(1),
    ADMIN(2);

    private final int clearance;

    CourseRole(int clearance) {
        this.clearance = clearance;
    }

    public boolean hasCollaboratorClearance() {
        return this.clearance >= COLLABORATOR.getClearance();
    }
}
