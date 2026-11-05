package org.example.Controller;

import javafx.scene.input.MouseEvent;
import org.example.Models.FormaGeometrica;
import org.example.Models.Poligono;
import org.example.Models.Ponto;

public class FerramentaDesenhar implements Ferramenta {
    private Poligono poligonoEmConstrucao = null;
    private double cursorX = 0;
    private double cursorY = 0;
    private boolean cursorAtivo = false;

    public double getCursorX() { return cursorX; }
    public double getCursorY() { return cursorY; }
    public boolean isCursorAtivo() { return cursorAtivo; }

    @Override
    public void aoPressionarMouse(MouseEvent e, EditorContext ctx) {}

    @Override
    public void aoArrastarMouse(MouseEvent e, EditorContext ctx) {}

    @Override
    public void aoSoltarMouse(MouseEvent e, EditorContext ctx) {}

    @Override
    public void aoMoverMouse(MouseEvent e, EditorContext ctx) {
        cursorX = ctx.aplicarSnap(e.getX());
        cursorY = ctx.aplicarSnap(e.getY());
        cursorAtivo = true;
        if (poligonoEmConstrucao != null) ctx.requestPaint();
    }

    @Override
    public void aoClicar(MouseEvent e, EditorContext ctx) {
        double x = ctx.aplicarSnap(e.getX());
        double y = ctx.aplicarSnap(e.getY());

        if (e.getClickCount() == 2) {
            // O clique simples (count=1) já disparou antes e adicionou um ponto duplicado: remover
            if (poligonoEmConstrucao != null && poligonoEmConstrucao.getQuantidadePontos() > 0) {
                poligonoEmConstrucao.getPontos().remove(poligonoEmConstrucao.getQuantidadePontos() - 1);
            }
            fecharPoligono(ctx);
            return;
        }

        if (poligonoEmConstrucao == null) {
            poligonoEmConstrucao = new Poligono();
            poligonoEmConstrucao.setCorPreenchimentoHex(ctx.getCorPreenchimentoHex());
            poligonoEmConstrucao.setCorBordaHex(ctx.getCorBordaHex());
            poligonoEmConstrucao.setEspessuraBorda(ctx.getEspessuraBorda());
        }
        poligonoEmConstrucao.adicionarPonto(new Ponto(x, y));
        ctx.requestPaint();
    }

    public void fecharPoligono(EditorContext ctx) {
        if (poligonoEmConstrucao != null && poligonoEmConstrucao.getQuantidadePontos() >= 3) {
            ctx.adicionarForma(poligonoEmConstrucao);
        }
        poligonoEmConstrucao = null;
        cursorAtivo = false;
        ctx.requestPaint();
    }

    public void cancelar() {
        poligonoEmConstrucao = null;
        cursorAtivo = false;
    }

    public Poligono getPoligonoEmConstrucao() { return poligonoEmConstrucao; }
}
