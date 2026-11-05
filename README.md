# Interactive Vector Drawing Editor — OOP N3

**Author:** João Henrique Porfirio Firmiano

**Course:** Object-Oriented Programming 1  
**Professor:** Prof. Me. Sergio Souza Novak  
**Universidade de Rio Verde — UniRV | Semester 2026/1**

---

## Description

Interactive 2D geometric shape editor built with Java 17 + JavaFX 21,
applying GoF design patterns (State, Prototype, Command/Memento, Composite)
and an MVC architecture.

---

## Requirements

- Java 17 LTS or higher
- Apache Maven 3.6+
- Internet connection on the first run (to download JavaFX through Maven)

---

## How to Run

```bash
cd editor-vetorial
mvn javafx:run
```

---

## Package Structure

```
src/main/java/org/example/
├── Models/           # Geometric models
│   ├── FormaGeometrica.java   (interface — Prototype)
│   ├── FormaBase.java         (abstract class)
│   ├── Ponto.java
│   ├── Poligono.java
│   ├── Circulo.java
│   ├── Quadrado.java          (vertices calculated automatically)
│   └── Hexagono.java          (vertices calculated automatically)
├── Controller/       # Control logic and tools
│   ├── Ferramenta.java              (interface — State)
│   ├── ModoFerramenta.java          (enum)
│   ├── EditorContext.java           (State Pattern context)
│   ├── HistoricoController.java     (Memento/Undo-Redo)
│   ├── FerramentaDesenhar.java
│   ├── FerramentaSelecionar.java
│   ├── FerramentaTransformar.java
│   ├── FerramentaInserirForma.java
│   └── FerramentaEditarVertice.java
├── Views/            # JavaFX graphical interface
│   ├── ContainerApp.java      (main Application)
│   ├── BarraLateralView.java
│   └── PainelStatusView.java
└── Persistence/
    └── PersistenciaXML.java   (save/load as XML)
```

---

## Design Patterns Implemented

### State Pattern
- `EditorContext` manages the active tool (state).
- `Ferramenta` (interface) exposes `aoPressionarMouse`, `aoArrastarMouse`, `aoSoltarMouse`, `aoClicar`.
- Concrete states: `FerramentaDesenhar`, `FerramentaSelecionar`, `FerramentaTransformar`,
  `FerramentaInserirForma`, `FerramentaEditarVertice`.

### Command + Memento (Undo/Redo)
- `HistoricoController` keeps `undoStack` and `redoStack`.
- `registrarEstado` saves deep copies through `clonar()` before each change.
- `desfazer` (Ctrl+Z) and `refazer` (Ctrl+Y) restore previous states.

### Prototype
- `FormaGeometrica.clonar()` is implemented in `Poligono`, `Circulo`, `Quadrado`, `Hexagono`.
- Ensures duplication without shared references in memory.

### Composite
- A shared `groupId` groups shapes logically.
- Translation and transformations are applied to the whole group at once.

---

## Features

### Tools
| Shortcut | Tool |
|----------|------|
| Sidebar | Free Line (polygon by clicks, double-click closes it) |
| Sidebar | Select (click / drag a box) |
| Sidebar | Edit Vertex (drag individual points) |
| Sidebar | Translate / Scale / Rotate / Shear |
| Sidebar | Insert Square / Circle / Hexagon (press and drag) |
| Delete | Delete selection |
| Ctrl+Z / Ctrl+Y | Undo / Redo |
| Ctrl+D | Duplicate selection |
| Ctrl+S | Save (XML) |
| Ctrl+O | Open (XML) |
| F | Close the polygon being drawn |
| Escape | Cancel operation / clear selection |

### Geometric Validations
- **Polygon**: at least 3 points to be added to the canvas.
- **Circle**: strictly positive radius (r > 0); throws `IllegalArgumentException`.
- **Square/Hexagon**: vertices calculated automatically from the center and radius.
- **Area**: Gauss (Shoelace) formula for polygons; π·r² for circles.
- **Perimeter**: sum of Euclidean distances; 2π·r for circles.

### Geometric Transformations
- **Translation**: moves each point by dx, dy.
- **Scale**: multiplies each point's distance to the centroid by the factor.
- **Rotation**: rotates points around the centroid by an angle in radians.
- **Shear**: applies the shx/shy factor to each coordinate.

### Snap to Grid
- Configurable visual grid (5–100 px) that rounds the cursor coordinates.

### XML Persistence
- Exports/imports shapes with all attributes (color, border, thickness, groupId, coordinates).
- Exception handling with a graphical alert; corrupted files do not crash the application.
- Warning when closing with unsaved changes.

---

## Encapsulation

All attributes of `FormaBase` and its subclasses are `protected`/`private`,
accessed through getters and setters as defined by the `FormaGeometrica` interface.
