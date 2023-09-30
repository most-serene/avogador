package eu.mostserene.avogador.filesystemservice.strox;

import lombok.Data;

import java.util.List;

@Data
public class Strox {
    private String codeExtension;
    private List<StroxCell> cells;
    private String path;

    public Strox() {
    }

    public Strox(String codeExtension, List<StroxCell> cells, String path) {
        this.codeExtension = codeExtension;
        this.cells = cells;
        this.path = path;
    }
}
