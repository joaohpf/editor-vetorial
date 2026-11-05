package org.example.Views;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.Controller.*;
import org.example.Models.*;
import org.example.Persistence.PersistenciaXML;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class ContainerApp extends Application {

    private EditorContext ctx;
    private Canvas canvas;
    private PainelStatusView painelStatus;
    private BarraLateralView barraLateral;
    private Stage stage;
    private double mouseX = 0, mouseY = 0;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        ctx = new EditorContext();
        ctx.setFerramentaAtual(new FerramentaSelecionar());
        ctx.setRequestPaintCallback(this::redesenhar);

        inicializarLayout(primaryStage);
        configurarEventosTeclado(primaryStage.getScene());
        redesenhar();
        primaryStage.show();
    }

    private void inicializarLayout(Stage stage) {
        canvas = new Canvas(1200, 900);
        Pane canvasPane = new Pane(canvas);
        canvasPane.setStyle("-fx-background-color: #f5f5f5;");
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());
        canvas.widthProperty().addListener(obs -> redesenhar());
        canvas.heightProperty().addListener(obs -> redesenhar());

        configurarEventosMouse();

        painelStatus = new PainelStatusView();
        barraLateral = new BarraLateralView(ctx, this::processarComandoLateral);

        BorderPane root = new BorderPane();
        root.setLeft(barraLateral);
        root.setCenter(canvasPane);
        root.setBottom(painelStatus);
        root.setStyle("-fx-background-color: #1e1e1e;");

        Scene scene = new Scene(root, 1280, 800);
        stage.setScene(scene);
        stage.setTitle("Editor de Desenho Vetorial — POO N3");
        stage.setOnCloseRequest(e -> {
            if (ctx.isModificado()) {
                Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                alerta.setTitle("Alterações não salvas");
                alerta.setHeaderText("Existem alterações não salvas.");
                alerta.setContentText("Deseja sair sem salvar?");
                Optional<ButtonType> resultado = alerta.showAndWait();
                if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
                    e.consume();
                }
            }
        });
    }

    private void configurarEventosMouse() {
        canvas.setOnMousePressed(e -> {
            Ferramenta ft = ctx.getFerramentaAtual();
            if (ft != null) ft.aoPressionarMouse(e, ctx);
        });
        canvas.setOnMouseDragged(e -> {
            Ferramenta ft = ctx.getFerramentaAtual();
            if (ft != null) ft.aoArrastarMouse(e, ctx);
            mouseX = e.getX(); mouseY = e.getY();
            painelStatus.atualizarCoordenadas(ctx.aplicarSnap(mouseX), ctx.aplicarSnap(mouseY));
        });
        canvas.setOnMouseReleased(e -> {
            Ferramenta ft = ctx.getFerramentaAtual();
            if (ft != null) ft.aoSoltarMouse(e, ctx);
        });
        canvas.setOnMouseClicked(e -> {
            Ferramenta ft = ctx.getFerramentaAtual();
            if (ft != null) ft.aoClicar(e, ctx);
            atualizarStatus();
        });
        canvas.setOnMouseMoved(e -> {
            mouseX = ctx.aplicarSnap(e.getX());
            mouseY = ctx.aplicarSnap(e.getY());
            painelStatus.atualizarCoordenadas(mouseX, mouseY);
            Ferramenta ftm = ctx.getFerramentaAtual();
            if (ftm != null) ftm.aoMoverMouse(e, ctx);
        });
    }

    private void configurarEventosTeclado(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.isControlDown()) {
                switch (e.getCode()) {
                    case Z -> ctx.desfazer();
                    case Y -> ctx.refazer();
                    case S -> salvar();
                    case O -> abrir();
                    case D -> duplicarSelecionadas();
                    default -> {}
                }
            } else {
                switch (e.getCode()) {
                    case DELETE, BACK_SPACE -> ctx.removerSelecionadas();
                    case ESCAPE -> {
                        Ferramenta ft = ctx.getFerramentaAtual();
                        if (ft instanceof FerramentaDesenhar fd) fd.cancelar();
                        ctx.limparSelecao();
                        redesenhar();
                    }
                    case F -> {
                        if (ctx.getFerramentaAtual() instanceof FerramentaDesenhar fd)
                            fd.fecharPoligono(ctx);
                    }
                    default -> {}
                }
            }
        });
    }

    private void processarComandoLateral(String cmd) {
        switch (cmd) {
            case "__SALVAR__" -> salvar();
            case "__ABRIR__" -> abrir();
            case "__LIMPAR__" -> limparTudo();
            default -> {
                painelStatus.atualizarFerramenta(cmd);
                atualizarStatus();
            }
        }
    }

    private void redesenhar() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.clearRect(0, 0, w, h);
        gc.setFill(Color.web("#f0f0f0"));
        gc.fillRect(0, 0, w, h);

        desenharGrade(gc, w, h);
        desenharFormas(gc);
        desenharFormaEmConstrucao(gc);
        desenharRetanguloSelecao(gc);
        atualizarStatus();
    }

    private void desenharGrade(GraphicsContext gc, double w, double h) {
        if (!ctx.isModoSnapping()) return;
        double g = ctx.getTamanhoGrid();
        gc.setStroke(Color.web("#dddddd"));
        gc.setLineWidth(0.5);
        for (double x = 0; x <= w; x += g) gc.strokeLine(x, 0, x, h);
        for (double y = 0; y <= h; y += g) gc.strokeLine(0, y, w, y);
    }

    private void desenharFormas(GraphicsContext gc) {
        List<FormaGeometrica> selecionadas = ctx.getFormasSelecionadas();

        for (FormaGeometrica f : ctx.getFormas()) {
            boolean selecionada = selecionadas.contains(f);

            if (f instanceof Circulo c) {
                desenharCirculo(gc, c, selecionada);
            } else {
                desenharPoligono(gc, f, selecionada);
            }
        }
    }

    private void desenharPoligono(GraphicsContext gc, FormaGeometrica f, boolean selecionada) {
        List<Ponto> pts = f.getPontos();
        if (pts.size() < 2) return;

        double[] xs = pts.stream().mapToDouble(Ponto::getX).toArray();
        double[] ys = pts.stream().mapToDouble(Ponto::getY).toArray();

        gc.setFill(Color.web(f.getCorPreenchimentoHex()));
        gc.fillPolygon(xs, ys, pts.size());

        gc.setStroke(selecionada ? Color.LIMEGREEN : Color.web(f.getCorBordaHex()));
        gc.setLineWidth(selecionada ? Math.max(f.getEspessuraBorda(), 2) : f.getEspessuraBorda());
        gc.strokePolygon(xs, ys, pts.size());

        if (selecionada) {
            // Desenhar handles nos vértices
            if (ctx.getFerramentaAtual() instanceof FerramentaEditarVertice) {
                gc.setFill(Color.RED);
                for (Ponto p : pts) {
                    gc.fillOval(p.getX() - 5, p.getY() - 5, 10, 10);
                }
            }
        }
    }

    private void desenharCirculo(GraphicsContext gc, Circulo c, boolean selecionada) {
        Ponto centro = c.getCentro();
        double r = c.getRaio();
        double x = centro.getX() - r;
        double y = centro.getY() - r;

        gc.setFill(Color.web(c.getCorPreenchimentoHex()));
        gc.fillOval(x, y, r * 2, r * 2);

        gc.setStroke(selecionada ? Color.LIMEGREEN : Color.web(c.getCorBordaHex()));
        gc.setLineWidth(selecionada ? Math.max(c.getEspessuraBorda(), 2) : c.getEspessuraBorda());
        gc.strokeOval(x, y, r * 2, r * 2);
    }

    private void desenharFormaEmConstrucao(GraphicsContext gc) {
        Ferramenta ft = ctx.getFerramentaAtual();
        if (!(ft instanceof FerramentaDesenhar fd)) return;
        Poligono pol = fd.getPoligonoEmConstrucao();
        if (pol == null || pol.getQuantidadePontos() == 0) return;

        List<Ponto> pts = pol.getPontos();
        double[] xs = pts.stream().mapToDouble(Ponto::getX).toArray();
        double[] ys = pts.stream().mapToDouble(Ponto::getY).toArray();

        gc.setFill(Color.web(pol.getCorPreenchimentoHex() + "88"));
        if (pts.size() >= 3) gc.fillPolygon(xs, ys, pts.size());

        gc.setStroke(Color.LIMEGREEN);
        gc.setLineWidth(1.5);
        gc.setLineDashes(5, 5);
        gc.strokePolyline(xs, ys, pts.size());

        // Linha rubber-band do último ponto até o cursor do mouse
        if (fd.isCursorAtivo()) {
            Ponto ultimo = pts.get(pts.size() - 1);
            gc.strokeLine(ultimo.getX(), ultimo.getY(), fd.getCursorX(), fd.getCursorY());
        }
        gc.setLineDashes(null);

        // Marcar vértice inicial em vermelho
        gc.setFill(Color.RED);
        gc.fillOval(pts.get(0).getX() - 5, pts.get(0).getY() - 5, 10, 10);
        // Marcar posição atual do cursor
        if (fd.isCursorAtivo()) {
            gc.setFill(Color.LIMEGREEN);
            gc.fillOval(fd.getCursorX() - 4, fd.getCursorY() - 4, 8, 8);
        }
    }

    private void desenharRetanguloSelecao(GraphicsContext gc) {
        Ferramenta ft = ctx.getFerramentaAtual();
        if (!(ft instanceof FerramentaSelecionar fs)) return;
        if (!fs.isArrastendoCaixa()) return;

        double x = Math.min(fs.getSelecaoStartX(), fs.getSelecaoAtualX());
        double y = Math.min(fs.getSelecaoStartY(), fs.getSelecaoAtualY());
        double w = Math.abs(fs.getSelecaoAtualX() - fs.getSelecaoStartX());
        double h = Math.abs(fs.getSelecaoAtualY() - fs.getSelecaoStartY());

        gc.setFill(Color.web("#5599ff33"));
        gc.fillRect(x, y, w, h);
        gc.setStroke(Color.web("#5599ff"));
        gc.setLineWidth(1);
        gc.setLineDashes(4, 4);
        gc.strokeRect(x, y, w, h);
        gc.setLineDashes(null);
    }

    private void atualizarStatus() {
        painelStatus.atualizarTotalFormas(ctx.getFormas().size());
        painelStatus.atualizarFormaSelecionada(ctx.getFormaSelecionada());
    }

    private void salvar() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Salvar Arquivo");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File arquivo = fc.showSaveDialog(stage);
        if (arquivo != null) {
            try {
                PersistenciaXML.salvar(ctx.getFormas(), arquivo);
                ctx.setModificado(false);
                stage.setTitle("Editor de Desenho Vetorial — " + arquivo.getName());
            } catch (Exception ex) {
                mostrarErro("Erro ao Salvar", "Não foi possível salvar o arquivo:\n" + ex.getMessage());
            }
        }
    }

    private void abrir() {
        if (ctx.isModificado()) {
            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Alterações não salvas");
            alerta.setHeaderText("Existem alterações não salvas.");
            alerta.setContentText("Deseja abrir um novo arquivo sem salvar as alterações?");
            Optional<ButtonType> r = alerta.showAndWait();
            if (r.isEmpty() || r.get() != ButtonType.OK) return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Abrir Arquivo");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File arquivo = fc.showOpenDialog(stage);
        if (arquivo != null) {
            try {
                List<FormaGeometrica> formas = PersistenciaXML.carregar(arquivo);
                ctx.getFormas().clear();
                ctx.getFormas().addAll(formas);
                ctx.limparSelecao();
                ctx.setModificado(false);
                stage.setTitle("Editor de Desenho Vetorial — " + arquivo.getName());
                redesenhar();
            } catch (Exception ex) {
                mostrarErro("Arquivo Corrompido", "Não foi possível carregar o arquivo:\n" + ex.getMessage());
            }
        }
    }

    private void limparTudo() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Limpar Tudo");
        alerta.setHeaderText("Deseja remover todas as formas?");
        Optional<ButtonType> r = alerta.showAndWait();
        if (r.isPresent() && r.get() == ButtonType.OK) {
            ctx.getHistorico().registrarEstado(ctx.getFormas());
            ctx.getFormas().clear();
            ctx.limparSelecao();
            ctx.setModificado(true);
            redesenhar();
        }
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

    private void mostrarErro(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
