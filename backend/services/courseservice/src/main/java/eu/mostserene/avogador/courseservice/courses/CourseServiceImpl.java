package eu.mostserene.avogador.courseservice.courses;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.courseservice.amqp.Sender;
import eu.mostserene.avogador.courseservice.storage.StorageService;
import eu.mostserene.avogador.courseservice.usercourses.UserCourse;
import eu.mostserene.avogador.courseservice.utils.WebSocketMessage;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Transactional
@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository repository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private Sender sender;

    private ObjectMapper mapper;

    @Value("${joinCode.secret}")
    private String joinCodeSecret;

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public Course createCourse(Course course) {
        course.setIsArchived(false);
        return repository.save(course);
    }

    @Override
    public Course updateCourse(UUID id, Course course) {
        course.setIsArchived(false);
        return repository.save(course);
    }

    @Override
    public Optional<Course> getCourse(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Course> getAll() {
        return repository.findAll();
    }

    @Override
    public void deleteCourse(UUID courseId) {
        repository.deleteById(courseId);
    }

    @Override
    public Course archiveCourse(UserCourse userCourse) {
        Course course = userCourse.getCourse();
        course.setIsArchived(true);
        repository.save(course);

        storageService.archiveCourse(course.getId(), (result, throwable) -> {
            if (result) {
                sender.send("users", "users.notify.socket",
                        new WebSocketMessage("/users/" + userCourse.getUser(),
                                "The course " + course.getName() + " has been archived successfully"
                        ));
            } else {
                course.setIsArchived(false);
                repository.save(course);
                sender.send("users", "users.notify.socket",
                        new WebSocketMessage("/users/" + userCourse.getUser(),
                                "The course " + course.getName() + " archiving has failed "
                        ));
            }
        });
        
        return course;
    }

    @Override
    public Optional<String> getJoinCode(UUID courseId) {
        Mac mac;
        String algorithm = "HmacSHA256";
        SecretKeySpec secretKeySpec = new SecretKeySpec(joinCodeSecret.getBytes(), algorithm);

        try {
            mac = Mac.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            return Optional.empty();
        }

        try {
            mac.init(secretKeySpec);
        } catch (InvalidKeyException e) {
            return Optional.empty();
        }

        String joinCode = bytesToHex(mac.doFinal(courseId.toString().getBytes()));
        return Optional.of(joinCode.length() > 20 ? joinCode.substring(joinCode.length() - 20) : joinCode);
    }

    @Override
    public Optional<Course> getByNameAndYear(String name, String year) {
        return repository.findByNameAndYear(name, year);
    }
}
