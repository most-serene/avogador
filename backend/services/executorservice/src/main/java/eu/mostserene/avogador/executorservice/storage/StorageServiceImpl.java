package eu.mostserene.avogador.executorservice.storage;

import eu.mostserene.avogador.executorservice.projectsubmission.ProjectSubmission;
import eu.mostserene.avogador.executorservice.submission.CodingSubmission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    @Override
    public File fetchAndSaveSubmissionCode(CodingSubmission codingSubmission) {
        RestTemplate restTemplate = new RestTemplate();
        String endpoint = "http://storage/courses/" + codingSubmission.getCourseId() +
                "/trials/" + codingSubmission.getTrialId() + "/exercises/" + codingSubmission.getExerciseId() + "/submissions/" +
                codingSubmission.getId() + "/source";
        byte[] archive = restTemplate.getForEntity(endpoint, byte[].class)
                .getBody();

        try {
            return Files.write(Paths.get("/avogador/" + codingSubmission.getId() + "/submission.tar.gz"), Objects.requireNonNull(archive))
                    .toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File fetchAndSaveTestcases(CodingSubmission codingSubmission) {
        RestTemplate restTemplate = new RestTemplate();
        String endpoint = "http://storage/courses/" + codingSubmission.getCourseId() +
                "/trials/" + codingSubmission.getTrialId() + "/exercises/" + codingSubmission.getExerciseId() + "/testcases";
        byte[] archive = restTemplate.getForEntity(endpoint, byte[].class)
                .getBody();

        try {
            return Files.write(Paths.get("/avogador/" + codingSubmission.getId() + "/testcases.tar.gz"), Objects.requireNonNull(archive))
                    .toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File fetchAndSaveProject(ProjectSubmission projectSubmission) {
        RestTemplate restTemplate = new RestTemplate();
        String endpoint = "http://storage/courses/" + projectSubmission.getCourseId() +
                "/projects/" + projectSubmission.getProjectId() +
                "/submissions/" + projectSubmission.getId();
        byte[] archive = restTemplate.getForEntity(endpoint, byte[].class)
                .getBody();

        try {
            return Files.write(Paths.get("/avogador/" + projectSubmission.getId() + "/submission.tar.gz"), Objects.requireNonNull(archive))
                    .toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void uploadNotebookExecutionLog(ProjectSubmission projectSubmission, File executionLog) {
        String endpoint = "http://storage/courses/" + projectSubmission.getCourseId() +
                "/projects/" + projectSubmission.getProjectId() +
                "/submissions/" + projectSubmission.getId() + "?filename=exec.out";

        uploadMultipartFileToStorage(endpoint, executionLog);
    }

    @Override
    public void uploadNotebookReport(ProjectSubmission projectSubmission, File report) {
        String endpoint = "http://storage/courses/" + projectSubmission.getCourseId() +
                "/projects/" + projectSubmission.getProjectId() +
                "/submissions/" + projectSubmission.getId() + "?filename=report.html";

        uploadMultipartFileToStorage(endpoint, report);
    }

    private void uploadMultipartFileToStorage(String endpoint, File file) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));

        restTemplate.put(endpoint, new HttpEntity<>(body, headers));
    }

}


/*
String requestUrl = "http://storage/courses/" + submission.getCourseId() +
                "/trials/" + submission.getTrialId() + "/exercises/" + submission.getExerciseId() + "/submissions/" +
                submission.getId() + "/source";  // Replace with the actual download URL
        String outputFile = "/avogador/output.tar.gz";  // Replace with the desired output file name

        try {
            // Create HttpClient instance
            HttpClient httpClient = HttpClientBuilder.create().build();

            // Send GET request
            HttpGet request = new HttpGet(requestUrl);
            HttpResponse response = httpClient.execute(request);

            // Check if response is successful (status code 200-299)
            if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 299) {
                // Read the response body as a byte array
                HttpEntity entity = response.getEntity();
                byte[] responseBody = EntityUtils.toByteArray(entity);

                // Save the response body to a file
                Files.write(Paths.get(outputFile), responseBody);
                System.out.println("File downloaded successfully!");
            } else {
                System.out.println("Failed to download file. Response code: " + response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

 */