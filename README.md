![build](https://status-api.mostserene.eu/projects/5?)
# Avogador
A system to perform coding exercises

*   [staging app deployed](https://avogador.staging.mostserene.eu/)
*   [staging API deployed](https://api.avogador.staging.mostserene.eu/)

## Pre Commit Hooks
To activate our custom pre commit hooks:

```git config core.hooksPath hooks```

At the moment, there are two hooks:
*  pre-commit executes unit tests on staged parts of the codebase
*  pre-push prevents push on master

From the repository root
