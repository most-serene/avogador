package eu.mostserene.avogador.usercourse.usercourses;

import eu.mostserene.avogador.usercourse.courses.Course;
import eu.mostserene.avogador.usercourse.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "UserCourses")
public class UserCourse {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment")
    @Column(columnDefinition = "serial")
    private Long id;

    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private User user;

    @JoinColumn(name = "course_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private Course course;

    @NotNull
    private CourseRole role = CourseRole.STUDENT;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    private Date joinDate = new Date(Calendar.getInstance().getTimeInMillis());

    public UserCourse() {
    }

    public UserCourse(User user, Course course, CourseRole role) {
        this.user = user;
        this.course = course;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
