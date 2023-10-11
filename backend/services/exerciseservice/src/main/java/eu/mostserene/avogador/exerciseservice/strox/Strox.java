package eu.mostserene.avogador.exerciseservice.strox;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Strox {
    private String sourceFileName;
    private List<StroxCell> cells;
    private String path;

    public Strox() {
    }

    public Strox(String sourceFileName, List<StroxCell> cells, String path) {
        this.sourceFileName = sourceFileName;
        this.cells = cells;
        this.path = path;
    }
}
