# dependency-analyzer

---

## Gradle Plugin to analyze dependencies of a project structure.

---

### Features

#### `printDependencyTree` Task
- Listing dependency tree of project.
- :warning: Only works currently when project does not contain
circular dependencies.
- :warning: This task is for testing.
Use gradle's built-in `dependencies` task instead.

#### `searchLibraryConnections` Task 
- Primary Goal: Analyzing connections of a transitive dependency:
  - Find root library - containing the transitive dependency -
  that is used directly by a project.
  - Find projects that are using the given dependency directly.
- Secondary objectives:
  - Display all unique dependencies in ascending order.
  - Display dependency graph statistics.
  - Display circular dependencies.

Tested with Gradle 7.5.1

---

### Usage

#### Plugin
- To add this plugin to your root project:
  - `id 'hu.web220.dependency-analyzer' version '1.0.0'` (Pending approval)

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
- Run `dependency-analyzer/printAllDependencies` gradle task
in the root project.
- Run `dependency-analyzer/searchLibraryConnections` gradle task
in the root project with the given parameters. (See `Usage`.)

---

### Contributions
[See contributing documentation](CONTRIBUTING.md).

---

### License
This repository is licensed under the [MIT License](LICENSE).