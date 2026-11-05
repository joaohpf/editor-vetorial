package org.example.Views;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.example.Controller.*;
import org.example.Models.*;

import java.util.function.Consumer;

public class BarraLateralView extends ScrollPane {
    private final EditorContext ctx;
    private final Consumer<String> onFerramentaMudou;

    // Controles de estilo
    private ColorPicker cpPreenchimento;
    private ColorPicker cpBorda;
    private Slider sliderEspessura;
    private Label lblEspessura;

    // Snapping
    private CheckBox chkSnap;
    private Slider sliderGrid;

    public BarraLateralView(EditorContext ctx, Consumer<String> onFerramentaMudou) {
        this.ctx = ctx;
        this.onFerramentaMudou = onFerramentaMudou;
        construir();
        setFitToWidth(true);
        setStyle("-fx-background-color: #1e1e1e;");
        setPrefWidth(200);
        setMinWidth(180);
    }

    private void construir() {
        VBox conteudo = new VBox(4);
        conteudo.setPadding(new Insets(8));
        conteudo.setStyle("-fx-background-color: #1e1e1e;");

        conteudo.getChildren().addAll(
            secao("ESTILO"),
            criarControlesEstilo(),
            secao("GRADE (Snapping)"),
            criarControlesSnap(),
            secao("FERRAMENTAS"),
            criarBotoesFerramentas(),
            secao("FORMAS"),
            criarBotoesFormas(),
            secao("CAMADAS/GRUPO"),
            criarBotoesCamadas(),
            secao("SISTEMA"),
            criarBotoesSistema()
        );

        setContent(conteudo);
    }

    private Label secao(String titulo) {
        Label l = new Label("— " + titulo + " —");
        l.setStyle("-fx-text-fill: #888888; -fx-font-size: 10px; -fx-padding: 6 0 2 0;");
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    private VBox criarControlesEstilo() {
        VBox box = new VBox(4);

        Label lblP = lbl("Cor Preenchimento:");
        cpPreenchimento = new ColorPicker(Color.web(ctx.getCorPreenchimentoHex()));
        cpPreenchimento.setMaxWidth(Double.MAX_VALUE);
        cpPreenchimento.setStyle("-fx-font-size: 11px;");
        cpPreenchimento.setOnAction(e -> {
            ctx.setCorPreenchimentoHex(toHex(cpPreenchimento.getValue()));
            aplicarCorSelecionada();
        });

        Label lblB = lbl("Cor Borda:");
        cpBorda = new ColorPicker(Color.web(ctx.getCorBordaHex()));
        cpBorda.setMaxWidth(Double.MAX_VALUE);
        cpBorda.setStyle("-fx-font-size: 11px;");
        cpBorda.setOnAction(e -> {
            ctx.setCorBordaHex(toHex(cpBorda.getValue()));
            aplicarCorBordaSelecionada();
        });

        lblEspessura = lbl("Espessura: 1.0");
        sliderEspessura = new Slider(0.5, 15, 1.0);
        sliderEspessura.setMajorTickUnit(5);
        sliderEspessura.setShowTickLabels(false);
        sliderEspessura.setMaxWidth(Double.MAX_VALUE);
        sliderEspessura.valueProperty().addListener((obs, o, n) -> {
            double v = Math.round(n.doubleValue() * 2) / 2.0;
            ctx.setEspessuraBorda(v);
            lblEspessura.setText(String.format("Espessura: %.1f", v));
            if (ctx.getFormaSelecionada() != null) {
                ctx.getFormaSelecionada().setEspessuraBorda(v);
                ctx.requestPaint();
            }
        });

        box.getChildren().addAll(lblP, cpPreenchimento, lblB, cpBorda, lblEspessura, sliderEspessura);
        return box;
    }

    private VBox criarControlesSnap() {
        VBox box = new VBox(4);

        chkSnap = new CheckBox("Grade Magnética (Snap)");
        chkSnap.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 11px;");
        chkSnap.setSelected(ctx.isModoSnapping());
        chkSnap.setOnAction(e -> ctx.setModoSnapping(chkSnap.isSelected()));

        Label lblG = lbl("Tamanho da grade:");
        sliderGrid = new Slider(5, 100, ctx.getTamanhoGrid());
        sliderGrid.setMajorTickUnit(20);
        sliderGrid.setShowTickLabels(true);
        sliderGrid.setMaxWidth(Double.MAX_VALUE);
        sliderGrid.valueProperty().addListener((obs, o, n) -> {
            ctx.setTamanhoGrid(n.doubleValue());
            ctx.requestPaint();
        });

        box.getChildren().addAll(chkSnap, lblG, sliderGrid);
        return box;
    }

    private VBox criarBotoesFerramentas() {
        VBox box = new VBox(3);
        box.getChildren().addAll(
            btn("✏ Linha Livre (Polígono)", () -> {
                ctx.setFerramentaAtual(new FerramentaDesenhar());
                onFerramentaMudou.accept("Linha Livre");
            }),
            btn("⬡ Selecionar", () -> {
                ctx.setFerramentaAtual(new FerramentaSelecionar());
                onFerramentaMudou.accept("Selecionar");
            }),
            btn("↕ Editar Vértice", () -> {
                ctx.setFerramentaAtual(new FerramentaEditarVertice());
                onFerramentaMudou.accept("Editar Vértice");
            }),
            btn("↔ Transladar", () -> {
                ctx.setFerramentaAtual(new FerramentaTransformar(ModoFerramenta.TRANSLADAR));
                onFerramentaMudou.accept("Transladar");
            }),
            btn("⤡ Escalar", () -> {
                ctx.setFerramentaAtual(new FerramentaTransformar(ModoFerramenta.ESCALAR));
                onFerramentaMudou.accept("Escalar");
            }),
            btn("↻ Rotacionar", () -> {
                ctx.setFerramentaAtual(new FerramentaTransformar(ModoFerramenta.ROTACIONAR));
                onFerramentaMudou.accept("Rotacionar");
            }),
            btn("⌓ Cisalhar", () -> {
                ctx.setFerramentaAtual(new FerramentaTransformar(ModoFerramenta.CISALHAR));
                onFerramentaMudou.accept("Cisalhar");
            }),
            btn("✕ Excluir seleção", () -> {
                ctx.removerSelecionadas();
                onFerramentaMudou.accept("-");
            }),
            btn("⎘ Duplicar seleção", () -> {
                duplicarSelecionadas();
            }),
            btn("↩ Desfazer (Ctrl+Z)", () -> ctx.desfazer()),
            btn("↪ Refazer (Ctrl+Y)", () -> ctx.refazer())
        );
        return box;
    }

    private VBox criarBotoesFormas() {
        VBox box = new VBox(3);
        box.getChildren().addAll(
            btn("□ Inserir Quadrado", () -> {
                ctx.setFerramentaAtual(new FerramentaInserirForma(FerramentaInserirForma.TipoForma.QUADRADO));
                onFerramentaMudou.accept("Inserir Quadrado");
            }),
            btn("○ Inserir Círculo", () -> {
                ctx.setFerramentaAtual(new FerramentaInserirForma(FerramentaInserirForma.TipoForma.CIRCULO));
                onFerramentaMudou.accept("Inserir Círculo");
            }),
            btn("⬡ Inserir Hexágono", () -> {
                ctx.setFerramentaAtual(new FerramentaInserirForma(FerramentaInserirForma.TipoForma.HEXAGONO));
                onFerramentaMudou.accept("Inserir Hexágono");
            }),
            btn("Fechar Polígono", () -> {
                Ferramenta ft = ctx.getFerramentaAtual();
                if (ft instanceof FerramentaDesenhar fd) fd.fecharPoligono(ctx);
            })
        );
        return box;
    }

    private VBox criarBotoesCamadas() {
        VBox box = new VBox(3);
        box.getChildren().addAll(
            btn("▲ Mover p/ Frente", () -> ctx.moverParaFrente(ctx.getFormaSelecionada())),
            btn("▼ Enviar p/ Trás", () -> ctx.enviarParaTras(ctx.getFormaSelecionada())),
            btn("⊞ Agrupar", () -> ctx.agruparSelecionadas()),
            btn("⊟ Desagrupar", () -> ctx.desagrupar())
        );
        return box;
    }

    private VBox criarBotoesSistema() {
        VBox box = new VBox(3);
        box.getChildren().addAll(
            btn("💾 Salvar", () -> onFerramentaMudou.accept("__SALVAR__")),
            btn("📂 Abrir", () -> onFerramentaMudou.accept("__ABRIR__")),
            btn("🗑 Limpar Tudo", () -> onFerramentaMudou.accept("__LIMPAR__"))
        );
        return box;
    }

    private Button btn(String texto, Runnable acao) {
        Button b = new Button(texto);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: #cccccc; -fx-font-size: 11px; -fx-border-color: #555; -fx-border-radius: 2; -fx-background-radius: 2;");
        b.setOnAction(e -> acao.run());
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: #4e5254; -fx-text-fill: #ffffff; -fx-font-size: 11px; -fx-border-color: #777; -fx-border-radius: 2; -fx-background-radius: 2;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: #cccccc; -fx-font-size: 11px; -fx-border-color: #555; -fx-border-radius: 2; -fx-background-radius: 2;"));
        return b;
    }

    private Label lbl(String texto) {
        Label l = new Label(texto);
        l.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 11px;");
        return l;
    }

    private String toHex(Color c) {
        return String.format("#%02X%02X%02X",
            (int)(c.getRed() * 255),
            (int)(c.getGreen() * 255),
            (int)(c.getBlue() * 255));
    }

    private void aplicarCorSelecionada() {
        for (FormaGeometrica f : ctx.getFormasSelecionadas()) {
            f.setCorPreenchimentoHex(ctx.getCorPreenchimentoHex());
        }
        ctx.requestPaint();
    }

    private void aplicarCorBordaSelecionada() {
        for (FormaGeometrica f : ctx.getFormasSelecionadas()) {
            f.setCorBordaHex(ctx.getCorBordaHex());
        }
        ctx.requestPaint();
    }

    private void duplicarSelecionadas() {
        if (ctx.getFormasSelecionadas().isEmpty()) return;
        ctx.getHistorico().registrarEstado(ctx.getFormas());
        for (FormaGeometrica f : new java.util.ArrayList<>(ctx.getFormasSelecionadas())) {
            FormaGeometrica copia = f.clonar();
            copia.transladar(20, 20);
            ctx.getFormas().add(copia);
        }
        ctx.setModificado(true);
        ctx.requestPaint();
    }

    public void sincronizarCores() {
        cpPreenchimento.setValue(Color.web(ctx.getCorPreenchimentoHex()));
        cpBorda.setValue(Color.web(ctx.getCorBordaHex()));
    }
}
