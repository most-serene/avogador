package eu.mostserene.avogador.exerciseservice.strox;

import lombok.Data;

@Data
public class StroxCell {
    private StroxCellType type;
    private String content;

    public StroxCell() {
    }

    public StroxCell(StroxCellType type, String content) {
        this.type = type;
        this.content = content;
    }
}
