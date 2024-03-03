# Release Notes of Dependency Analyzer

---

## `v1.2.0`
Release date: `2024-03-03`
### Optimization:
- Only rebuild dependency graph 
  when a `build.gradle` file has changed
  in the project structure
  after the last graph build.
### Security:
- Added regular expression check for the `lib` parameter's value.
### Documentation:
- Added versioning clarification to [README](README.md#versioning)
- Added this release notes document.
- Fixed anchors and links in md files.
---

## `v1.1.1`
Release date: `2023-10-25`
### Features:
- Search one library in the dependency trees of the whole project structure
  and find its first level dependency that is directly used by a project.
- Also list the user projects of the found first level dependencies.
- Circular dependency detection.
- Statistics of the dependency graph created.
- Listing all unique dependency ids of the whole project structure.
- Basic test.