package org.example.Controller;

import javafx.scene.input.MouseEvent;
import org.example.Models.FormaGeometrica;
import org.example.Models.Ponto;

import java.util.List;

public class FerramentaEditarVertice implements Ferramenta {
    private Ponto verticeSelecionado = null;
    private static final double RAIO_SELECAO = 8.0;

    public Ponto getVerticeSelecionado() { return verticeSelecionado; }

    @Override
    public void aoPressionarMouse(MouseEvent e, EditorContext ctx) {
        verticeSelecionado = null;
        FormaGeometrica forma = ctx.getFormaSelecionada();
        if (forma == null) {
            forma = ctx.encontrarFormaNoPonto(e.getX(), e.getY());
            if (forma != null) ctx.selecionarForma(forma);
        }
        if (forma == null) return;

        List<Ponto> pontos = forma.getPontos();
        for (Ponto p : pontos) {
            double dx = p.getX() - e.getX();
            double dy = p.getY() - e.getY();
            if (Math.sqrt(dx * dx + dy * dy) <= RAIO_SELECAO) {
                verticeSelecionado = p;
                ctx.getHistorico().registrarEstado(ctx.getFormas());
                break;
            }
        }
    }

    @Override
    public void aoArrastarMouse(MouseEvent e, EditorContext ctx) {
        if (verticeSelecionado != null) {
            verticeSelecionado.setX(ctx.aplicarSnap(e.getX()));
            verticeSelecionado.setY(ctx.aplicarSnap(e.getY()));
            ctx.setModificado(true);
            ctx.requestPaint();
        }
    }

    @Override
    public void aoSoltarMouse(MouseEvent e, EditorContext ctx) {
        verticeSelecionado = null;
    }

    @Override
    public void aoClicar(MouseEvent e, EditorContext ctx) {
        if (ctx.getFormaSelecionada() == null) {
            FormaGeometrica f = ctx.encontrarFormaNoPonto(e.getX(), e.getY());
            ctx.selecionarForma(f);
        }
    }
}
