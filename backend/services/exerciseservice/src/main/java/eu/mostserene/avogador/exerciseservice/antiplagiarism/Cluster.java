package eu.mostserene.avogador.exerciseservice.antiplagiarism;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class Cluster {
    private double averageSimilarity;
    private double strength;
    private Set<UUID> members;

    public Cluster() {
    }

    public Cluster(double averageSimilarity, double strength, Set<UUID> members) {
        this.averageSimilarity = averageSimilarity;
        this.strength = strength;
        this.members = members;
    }
}
