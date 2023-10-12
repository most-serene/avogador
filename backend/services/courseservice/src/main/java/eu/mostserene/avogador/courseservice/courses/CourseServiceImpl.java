package eu.mostserene.avogador.courseservice.courses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService{
    @Autowired
    private CourseRepository repository;

    @Value("${joinCode.secret}")
    private String joinCodeSecret;

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
    public void deleteCourse(UUID courseId) {
        repository.deleteById(courseId);
    }

    @Override
    public Optional<String> getJoinCode(UUID courseId){
        Mac mac;
        String algorithm = "HmacSHA256";
        SecretKeySpec secretKeySpec = new SecretKeySpec(joinCodeSecret.getBytes(), algorithm);

        try {
            mac = Mac.getInstance(algorithm);
        }catch (NoSuchAlgorithmException e){
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
    public Optional<Course> getByNameAndYear(String name, String year) {
        return repository.findByNameAndYear(name, year);
    }
}
