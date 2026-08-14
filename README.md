# NotesApp

A notes app built with Jetpack Compose, following MVVM architecture.
Built as part of a structured Android development learning plan.

## Features
- Create, view, and delete notes
- List → detail navigation with nav arguments
- Light/dark theme toggle (Material3)
- Configuration-change-safe (rotation)

## Architecture
- **MVVM**: `NotesViewModel` exposes `StateFlow<List<Note>>`
- **Repository pattern**: `NotesRepository` interface decouples ViewModel 
  from the data source (currently in-memory, Room coming next)
- **Navigation Compose**: note ID passed via nav argument (`Display/{id}`), 
  detail screen re-derives state from ViewModel — no duplicated state

## Screens
| Screen | Route | Description |
|---|---|---|
| Notes | `Notes` | List all notes, tap to view, delete inline |
| Create | `Create` | Add a new note |
| Display | `Display/{id}` | Full note view |

## Tech stack
Kotlin · Jetpack Compose · Material3 · Navigation Compose · StateFlow

## Status
🚧 In progress — Room DB persistence coming next (Week 11)

## Screenshots
_(add before pushing — list screen, detail screen, dark mode)_