# dependency-analyzer

---

## Gradle Plugin to analyze dependencies of a project structure.

---

### To add this plugin to your project:
[Visit Gradle Plugin Portal](https://plugins.gradle.org/plugin/hu.web220.dependency-analyzer)

---

### Tested with:
- Gradle 7.5.1
- Java 8
- Java 17

---

### Features

#### `searchLibraryConnections` Task 
- Primary Goal: Analyzing connections of a transitive dependency:
  - Find root library (direct library dependency / first-level library dependency of a project) - containing the transitive dependency -
  that is used directly by a project.
  - Find projects that are using the given dependency directly.
- Secondary objectives:
  - Display all unique dependencies in ascending order.
  - Display dependency graph statistics.
  - Display circular dependencies.

---

### <a name="usage"></a>Usage

#### searchLibraryConnections task:
You must call this gradle task with the `lib` parameter,
giving it the `{groupId}:{name}:{version}` library identifier.

Optional flag parameters are:
- `list`: Display all unique dependencies in name order.
- `stats`: Display the node and connection count
in the dependency graph.
- `circular`: Display circular dependency chains.

---

### Manual Testing
- Run `publishing/publishToMavenLocal` gradle task
in `core` module.
- Run `dependency-analyzer/searchLibraryConnections` gradle task
in the root project with the given parameters. (See [Usage](#usage))

---

### Contributions
[See contributing documentation](CONTRIBUTING.md).

---

### License
This repository is licensed under the [MIT License](LICENSE).