package eu.mostserene.avogador.exerciseservice.filesystem;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FileSystemServiceImpl implements FileSystemService {

    @Override
    public void createTrial(Trial trial) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public void deleteTrial(Trial trial) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public void createExercise(Exercise exercise) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public void deleteExercise(Exercise exercise) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
}
