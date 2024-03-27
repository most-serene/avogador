package eu.mostserene.avogador.exerciseservice.projectservice.notebookprojects;

import eu.mostserene.avogador.exerciseservice.trials.ProgrammingLanguage;

public enum NotebookKernel {
    IPYKERNEL("IPyKernel", ProgrammingLanguage.PYTHON);

    NotebookKernel(String name, ProgrammingLanguage language) {
    }
}
