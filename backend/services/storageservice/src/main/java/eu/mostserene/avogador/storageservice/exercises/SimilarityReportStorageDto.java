package eu.mostserene.avogador.storageservice.exercises;

import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Data
public class SimilarityReportStorageDto {
    private UUID courseId;
    private UUID trialId;
    private UUID exerciseId;
    private byte[] similarityReportZip;

    public SimilarityReportStorageDto() {
    }

    public SimilarityReportStorageDto(UUID courseId, UUID trialId, UUID exerciseId, File similarityReportZipFile) throws IOException {
        this.courseId = courseId;
        this.trialId = trialId;
        this.exerciseId = exerciseId;
        this.similarityReportZip = FileUtils.readFileToByteArray(similarityReportZipFile);;
    }
}
