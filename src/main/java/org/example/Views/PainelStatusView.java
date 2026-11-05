package org.example.Views;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.example.Models.FormaGeometrica;

public class PainelStatusView extends HBox {
    private final Label lblCoordenadas = new Label("X: 0  Y: 0");
    private final Label lblTotalFormas = new Label("Formas: 0");
    private final Label lblArea = new Label("Área: -");
    private final Label lblPerimetro = new Label("Perímetro: -");
    private final Label lblFerramenta = new Label("Ferramenta: -");

    public PainelStatusView() {
        setSpacing(16);
        setPadding(new Insets(4, 8, 4, 8));
        setStyle("-fx-background-color: #2b2b2b; -fx-border-color: #444 transparent transparent transparent;");

        for (Label l : new Label[]{lblCoordenadas, lblTotalFormas, lblArea, lblPerimetro, lblFerramenta}) {
            l.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 11px;");
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(lblCoordenadas, sep(), lblTotalFormas, sep(), lblArea, sep(), lblPerimetro, spacer, lblFerramenta);
    }

    private Label sep() {
        Label l = new Label("|");
        l.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px;");
        return l;
    }

    public void atualizarCoordenadas(double x, double y) {
        lblCoordenadas.setText(String.format("X: %.1f  Y: %.1f", x, y));
    }

    public void atualizarTotalFormas(int total) {
        lblTotalFormas.setText("Formas: " + total);
    }

    public void atualizarFormaSelecionada(FormaGeometrica f) {
        if (f == null) {
            lblArea.setText("Área: -");
            lblPerimetro.setText("Perímetro: -");
        } else {
            lblArea.setText(String.format("Área: %.2f", f.calcularArea()));
            lblPerimetro.setText(String.format("Perímetro: %.2f", f.calcularPerimetro()));
        }
    }

    public void atualizarFerramenta(String nome) {
        lblFerramenta.setText("Ferramenta: " + nome);
    }
}
