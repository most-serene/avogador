package eu.mostserene.avogador.filesystemservice.testcases;

import lombok.Data;

import java.util.UUID;

@Data
public class TestcaseResponseTDO {
    private UUID testcaseId;
    private String input;
    private String output;

    public TestcaseResponseTDO() {
    }

    public TestcaseResponseTDO(UUID testcaseId, String input, String output) {
        this.testcaseId = testcaseId;
        this.input = input;
        this.output = output;
    }
}
