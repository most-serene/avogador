package eu.mostserene.avogador.courseservice.usercourses;

import lombok.Getter;

@Getter
public enum CourseRole {
    EXTERNAL(-1),
    STUDENT(0),
    COLLABORATOR(1),
    ADMIN(2);

    private final Integer clearance;

    CourseRole(Integer clearance) {
        this.clearance = clearance;
    }

}
