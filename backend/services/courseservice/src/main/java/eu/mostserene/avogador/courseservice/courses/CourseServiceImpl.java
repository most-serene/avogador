package eu.mostserene.avogador.courseservice.courses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

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
    public Course updateCourse(Long id, Course course) {
        course.setIsArchived(false);
        return repository.save(course);
    }

    @Override
    public Optional<Course> getCourse(Long id) {
        return repository.findById(id);
    }

    @Override
    public void deleteCourse(Long courseId) {
        repository.deleteById(courseId);
    }

    @Override
    public String getJoinCode(Long courseId) throws NoSuchAlgorithmException, InvalidKeyException {
        String algorithm = "HmacSHA256";
        SecretKeySpec secretKeySpec = new SecretKeySpec(joinCodeSecret.getBytes(), algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKeySpec);

        return bytesToHex(mac.doFinal(courseId.toString().getBytes()));
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

}
