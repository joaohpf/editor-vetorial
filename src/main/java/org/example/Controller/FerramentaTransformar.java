package org.example.Controller;

import javafx.scene.input.MouseEvent;
import org.example.Models.FormaGeometrica;

public class FerramentaTransformar implements Ferramenta {
    private ModoFerramenta modo;
    private double pressX, pressY;

    public FerramentaTransformar(ModoFerramenta modo) {
        this.modo = modo;
    }

    public ModoFerramenta getModo() { return modo; }
    public void setModo(ModoFerramenta modo) { this.modo = modo; }

    @Override
    public void aoPressionarMouse(MouseEvent e, EditorContext ctx) {
        pressX = e.getX();
        pressY = e.getY();
        ctx.getHistorico().registrarEstado(ctx.getFormas());
    }

    @Override
    public void aoArrastarMouse(MouseEvent e, EditorContext ctx) {
        if (ctx.getFormasSelecionadas().isEmpty()) return;

        double dx = e.getX() - pressX;
        double dy = e.getY() - pressY;

        for (FormaGeometrica f : ctx.getFormasSelecionadas()) {
            switch (modo) {
                case TRANSLADAR -> f.transladar(dx, dy);
                case ESCALAR -> {
                    double fator = 1.0 + dy * 0.005;
                    if (fator > 0.01) f.escalar(fator);
                }
                case ROTACIONAR -> {
                    double angulo = dx * 0.01;
                    f.rotacionar(angulo);
                }
                case CISALHAR -> {
                    f.cisalhar(dx * 0.002, dy * 0.002);
                }
            }
        }
        pressX = e.getX();
        pressY = e.getY();
        ctx.setModificado(true);
        ctx.requestPaint();
    }

    @Override
    public void aoSoltarMouse(MouseEvent e, EditorContext ctx) {}

    @Override
    public void aoClicar(MouseEvent e, EditorContext ctx) {
        FormaGeometrica f = ctx.encontrarFormaNoPonto(e.getX(), e.getY());
        ctx.selecionarForma(f);
    }
}
