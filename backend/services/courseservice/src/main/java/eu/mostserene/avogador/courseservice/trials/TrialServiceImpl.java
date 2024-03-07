package eu.mostserene.avogador.courseservice.trials;

import eu.mostserene.avogador.courseservice.amqp.Sender;
import eu.mostserene.avogador.courseservice.courses.Course;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TrialServiceImpl implements TrialService {

    @Autowired
    private Sender sender;

    @Override
    public void deleteByCourse(Course course) {
        sender.send("exercises", "trials.delete", course.getId().toString());
    }
}
