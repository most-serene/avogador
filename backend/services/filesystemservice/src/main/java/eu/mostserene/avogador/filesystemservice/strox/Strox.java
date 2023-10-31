package eu.mostserene.avogador.filesystemservice.strox;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Strox {
    private String sourceFileName;
    private List<StroxCell> cells;
    private String path;
    private Map<String, String> outputs = new HashMap<>();

    static public Strox merge(Strox template, Strox submission) {
        Strox merged = new Strox();
        merged.setSourceFileName(template.getSourceFileName());

        merged.setCells(new ArrayList<>());
        merged.getCells().addAll(template.getCells());

        merged.getCells().stream().filter(stroxCell -> stroxCell.getType() == StroxCellType.EDITABLE)
                .forEach(stroxCell -> stroxCell.setContent(submission.getCells().remove(0).getContent()));

        return merged;
    }

    public Strox() {
    }

    public Strox(String sourceFileName, List<StroxCell> cells, String path) {
        this.sourceFileName = sourceFileName;
        this.cells = cells;
        this.path = path;
    }

    public String generateSourceCode() {
        return String.join("\n", getCells().stream().map(StroxCell::getContent).toList());
    }
}
