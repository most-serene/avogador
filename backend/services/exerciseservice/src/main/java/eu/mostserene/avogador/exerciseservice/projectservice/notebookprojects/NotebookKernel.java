package eu.mostserene.avogador.exerciseservice.projectservice.notebookprojects;

import eu.mostserene.avogador.exerciseservice.trials.ProgrammingLanguage;

public enum NotebookKernel {
    IPYKERNEL("IPyKernel", ProgrammingLanguage.PYTHON);


    final String name;
    final ProgrammingLanguage language;

    NotebookKernel(String name, ProgrammingLanguage language) {
        this.name = name;
        this.language = language;
    }
}
