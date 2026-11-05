package org.example.Controller;

import javafx.scene.input.MouseEvent;
import org.example.Models.*;

public class FerramentaInserirForma implements Ferramenta {
    public enum TipoForma { QUADRADO, CIRCULO, HEXAGONO }

    private final TipoForma tipo;
    private Ponto centroInsercao = null;
    private double pressX, pressY;

    public FerramentaInserirForma(TipoForma tipo) {
        this.tipo = tipo;
    }

    public Ponto getCentroInsercao() { return centroInsercao; }

    @Override
    public void aoPressionarMouse(MouseEvent e, EditorContext ctx) {
        pressX = ctx.aplicarSnap(e.getX());
        pressY = ctx.aplicarSnap(e.getY());
        centroInsercao = new Ponto(pressX, pressY);
    }

    @Override
    public void aoArrastarMouse(MouseEvent e, EditorContext ctx) {
        ctx.requestPaint();
    }

    @Override
    public void aoSoltarMouse(MouseEvent e, EditorContext ctx) {
        double cx = ctx.aplicarSnap(pressX);
        double cy = ctx.aplicarSnap(pressY);
        double rx = ctx.aplicarSnap(e.getX());
        double ry = ctx.aplicarSnap(e.getY());

        double dx = rx - cx;
        double dy = ry - cy;
        double raio = Math.sqrt(dx * dx + dy * dy);
        if (raio < 5) raio = 50;

        Ponto centro = new Ponto(cx, cy);
        FormaGeometrica forma = switch (tipo) {
            case QUADRADO -> new Quadrado(centro, raio);
            case CIRCULO  -> new Circulo(centro, raio);
            case HEXAGONO -> new Hexagono(centro, raio);
        };
        forma.setCorPreenchimentoHex(ctx.getCorPreenchimentoHex());
        forma.setCorBordaHex(ctx.getCorBordaHex());
        forma.setEspessuraBorda(ctx.getEspessuraBorda());

        ctx.adicionarForma(forma);
        centroInsercao = null;
    }

    @Override
    public void aoClicar(MouseEvent e, EditorContext ctx) {}
}
