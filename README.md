# Editor de Desenho Vetorial Interativo — POO N3

**Disciplina:** Programação Orientada a Objetos 1  
**Docente:** Prof. Me. Sergio Souza Novak  
**Universidade de Rio Verde — UniRV | Semestre 2026/1**

---

## Descrição

Editor de formas geométricas 2D interativo desenvolvido em Java 17 + JavaFX 21,
aplicando os padrões de projeto GoF (State, Prototype, Command/Memento, Composite)
e separação arquitetural MVC.

---

## Requisitos

- Java 17 LTS ou superior
- Apache Maven 3.6+
- Conexão com a internet na primeira execução (para baixar JavaFX via Maven)

---

## Como Executar

```bash
cd editor-vetorial
mvn javafx:run
```

---

## Estrutura de Pacotes

```
src/main/java/org/example/
├── Models/           # Modelos geométricos
│   ├── FormaGeometrica.java   (interface — Prototype)
│   ├── FormaBase.java         (classe abstrata)
│   ├── Ponto.java
│   ├── Poligono.java
│   ├── Circulo.java
│   ├── Quadrado.java          (vértices calculados automaticamente)
│   └── Hexagono.java          (vértices calculados automaticamente)
├── Controller/       # Lógica de controle e ferramentas
│   ├── Ferramenta.java              (interface — State)
│   ├── ModoFerramenta.java          (enum)
│   ├── EditorContext.java           (contexto do State Pattern)
│   ├── HistoricoController.java     (Memento/Undo-Redo)
│   ├── FerramentaDesenhar.java
│   ├── FerramentaSelecionar.java
│   ├── FerramentaTransformar.java
│   ├── FerramentaInserirForma.java
│   └── FerramentaEditarVertice.java
├── Views/            # Interface gráfica JavaFX
│   ├── ContainerApp.java      (Application principal)
│   ├── BarraLateralView.java
│   └── PainelStatusView.java
└── Persistence/
    └── PersistenciaXML.java   (salvar/carregar em XML)
```

---

## Padrões de Projeto Implementados

### State Pattern
- `EditorContext` gerencia a ferramenta ativa (estado).
- `Ferramenta` (interface) expõe `aoPressionarMouse`, `aoArrastarMouse`, `aoSoltarMouse`, `aoClicar`.
- Estados concretos: `FerramentaDesenhar`, `FerramentaSelecionar`, `FerramentaTransformar`,
  `FerramentaInserirForma`, `FerramentaEditarVertice`.

### Command + Memento (Undo/Redo)
- `HistoricoController` mantém pilhas `undoStack` e `redoStack`.
- `registrarEstado` salva cópias profundas via `clonar()` antes de cada mutação.
- `desfazer` (Ctrl+Z) e `refazer` (Ctrl+Y) restauram estados anteriores.

### Prototype
- `FormaGeometrica.clonar()` implementado em `Poligono`, `Circulo`, `Quadrado`, `Hexagono`.
- Garante duplicação sem compartilhamento de referências em memória.

### Composite
- `groupId` comum agrupa formas logicamente.
- Translação e transformações aplicadas a todo o grupo de uma vez.

---

## Funcionalidades

### Ferramentas
| Atalho | Ferramenta |
|--------|------------|
| Barra lateral | Linha Livre (polígono por cliques, duplo-clique fecha) |
| Barra lateral | Selecionar (clique/arrastar caixa) |
| Barra lateral | Editar Vértice (arrastar pontos individuais) |
| Barra lateral | Transladar / Escalar / Rotacionar / Cisalhar |
| Barra lateral | Inserir Quadrado / Círculo / Hexágono (pressionar e arrastar) |
| Delete | Excluir seleção |
| Ctrl+Z / Ctrl+Y | Desfazer / Refazer |
| Ctrl+D | Duplicar seleção |
| Ctrl+S | Salvar (XML) |
| Ctrl+O | Abrir (XML) |
| F | Fechar polígono em construção |
| Escape | Cancelar operação / limpar seleção |

### Validações Geométricas
- **Polígono**: mínimo 3 pontos para ser adicionado ao canvas.
- **Círculo**: raio estritamente positivo (r > 0); lança `IllegalArgumentException`.
- **Quadrado/Hexágono**: vértices calculados automaticamente a partir do centro e raio.
- **Área**: Fórmula de Gauss (Shoelace) para polígonos; π·r² para círculos.
- **Perímetro**: Soma de distâncias euclidianas; 2π·r para círculos.

### Transformações Geométricas
- **Translação**: desloca dx, dy em cada ponto.
- **Escala**: multiplica distância de cada ponto ao centroide pelo fator.
- **Rotação**: rotaciona pontos em torno do centroide por ângulo em radianos.
- **Cisalhamento**: aplica fator shx/shy a cada coordenada.

### Snap to Grid
- Grade visual configurável (5–100 px) com arredondamento de coordenadas do cursor.

### Persistência XML
- Exporta/importa formas com todos os atributos (cor, borda, espessura, groupId, coordenadas).
- Tratamento de exceções com alerta gráfico; arquivos corrompidos não interrompem a aplicação.
- Alerta ao fechar com alterações não salvas.

---

## Encapsulamento

Todos os atributos de `FormaBase` e subclasses são `protected`/`private`,
com acesso via getters e setters conforme o contrato da interface `FormaGeometrica`.

---

## Critérios de Avaliação Atendidos

| Critério | Peso | Status |
|----------|------|--------|
| Modelagem Geométrica e Validações Matemáticas | 25% | ✅ |
| Implementação de Padrões (State, Prototype, Memento) | 35% | ✅ |
| Usabilidade da GUI em JavaFX e Controles do Mouse | 20% | ✅ |
| Persistência XML e Tratamento de Erros | 20% | ✅ |
