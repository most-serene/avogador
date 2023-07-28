package eu.mostserene.avogador.usercourse.usercourses;

import eu.mostserene.avogador.usercourse.courses.Course;
import eu.mostserene.avogador.usercourse.users.UserDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import java.util.Calendar;
import java.util.Date;

@Entity
@Table(
        name = "UserCourses",
        uniqueConstraints = @UniqueConstraint(columnNames={"userId", "course_id"})
)
public class UserCourse {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment")
    @Column(columnDefinition = "serial")
    private Long id;

    @NotNull
    private Long userId;

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

    public Long getId() {
        return id;
    }

    public Long getUser() {
        return userId;
    }

    public void setUser(UserDto user) {
        this.userId = user.getId();
    }
    public void setUser(Long id) {
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
