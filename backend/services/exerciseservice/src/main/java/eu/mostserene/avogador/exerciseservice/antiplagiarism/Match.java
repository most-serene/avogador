package eu.mostserene.avogador.exerciseservice.antiplagiarism;

import lombok.Data;

@Data
public class Match {
    private String firstFile;
    private String secondFile;
    private int firstStart;
    private int firstEnd;
    private int secondStart;
    private int secondEnd;
    private int tokens;

    public Match() {
    }

    public Match(String firstFile, String secondFile, int firstStart, int firstEnd, int secondStart, int secondEnd, int tokens) {
        this.firstFile = firstFile;
        this.secondFile = secondFile;
        this.firstStart = firstStart;
        this.firstEnd = firstEnd;
        this.secondStart = secondStart;
        this.secondEnd = secondEnd;
        this.tokens = tokens;
    }
}
