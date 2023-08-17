package eu.mostserene.avogador.courseservice.usercourses;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.users.UserDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(
        name = "UserCourses",
        uniqueConstraints = @UniqueConstraint(columnNames={"userId", "course_id"})
)
public class UserCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private UUID userId;

    @JoinColumn(name = "course_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private Course course;

    @NotNull
    private CourseRole role = CourseRole.STUDENT;

    @NotNull
    private Date joinDate = new Date(Calendar.getInstance().getTimeInMillis());

    public UserCourse() {
    }

    public UserCourse(UserDto user, Course course, CourseRole role) {
        this.userId = user.getId();
        this.course = course;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUser() {
        return userId;
    }

    public void setUser(UserDto user) {
        this.userId = user.getId();
    }
    public void setUser(UUID id) {
        this.userId = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public CourseRole getRole() {
        return role;
    }

    public void setRole(CourseRole role) {
        this.role = role;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }
}
