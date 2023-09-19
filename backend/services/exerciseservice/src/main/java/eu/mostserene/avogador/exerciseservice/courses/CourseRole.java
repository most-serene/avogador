package eu.mostserene.avogador.exerciseservice.courses;

public enum CourseRole {
    EXTERNAL(-1),
    STUDENT(0),
    COLLABORATOR(1),
    ADMIN(2);

    private final Integer clearance;

    CourseRole(Integer clearance){
        this.clearance = clearance;
    }

    public Integer getClearance(){
        return this.clearance;
    }
}
