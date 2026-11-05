package org.example.Controller;

import javafx.scene.input.MouseEvent;
import org.example.Models.FormaGeometrica;

import java.util.ArrayList;
import java.util.List;

public class FerramentaSelecionar implements Ferramenta {
    private boolean arrastendoCaixa = false;
    private double selecaoStartX = 0;
    private double selecaoStartY = 0;
    private double selecaoAtualX = 0;
    private double selecaoAtualY = 0;

    // Para arrastar forma selecionada
    private boolean arrastendoForma = false;
    private double dragStartX = 0;
    private double dragStartY = 0;

    public double getSelecaoStartX() { return selecaoStartX; }
    public double getSelecaoStartY() { return selecaoStartY; }
    public double getSelecaoAtualX() { return selecaoAtualX; }
    public double getSelecaoAtualY() { return selecaoAtualY; }
    public boolean isArrastendoCaixa() { return arrastendoCaixa; }

    @Override
    public void aoPressionarMouse(MouseEvent e, EditorContext ctx) {
        double x = e.getX();
        double y = e.getY();

        FormaGeometrica clicada = ctx.encontrarFormaNoPonto(x, y);

        if (clicada != null) {
            ctx.getHistorico().registrarEstado(ctx.getFormas());
            // Verificar se pertence a grupo
            if (clicada.getGroupId() != null) {
                List<FormaGeometrica> grupo = new ArrayList<>();
                for (FormaGeometrica f : ctx.getFormas()) {
                    if (clicada.getGroupId().equals(f.getGroupId())) grupo.add(f);
                }
                ctx.setFormasSelecionadas(grupo);
                ctx.setFormaSelecionada(clicada);
            } else {
                ctx.selecionarForma(clicada);
            }
            arrastendoForma = true;
            dragStartX = x;
            dragStartY = y;
            arrastendoCaixa = false;
        } else {
            ctx.limparSelecao();
            arrastendoCaixa = true;
            arrastendoForma = false;
            selecaoStartX = x;
            selecaoStartY = y;
            selecaoAtualX = x;
            selecaoAtualY = y;
        }
        ctx.requestPaint();
    }

    @Override
    public void aoArrastarMouse(MouseEvent e, EditorContext ctx) {
        if (arrastendoForma && !ctx.getFormasSelecionadas().isEmpty()) {
            double dx = e.getX() - dragStartX;
            double dy = e.getY() - dragStartY;
            for (FormaGeometrica f : ctx.getFormasSelecionadas()) {
                f.transladar(dx, dy);
            }
            dragStartX = e.getX();
            dragStartY = e.getY();
            ctx.setModificado(true);
            ctx.requestPaint();
        } else if (arrastendoCaixa) {
            selecaoAtualX = e.getX();
            selecaoAtualY = e.getY();
            ctx.requestPaint();
        }
    }

    @Override
    public void aoSoltarMouse(MouseEvent e, EditorContext ctx) {
        if (arrastendoCaixa) {
            selecionarFormasDaRegiao(ctx);
            arrastendoCaixa = false;
            ctx.requestPaint();
        }
        if (arrastendoForma) {
            ctx.setModificado(true);
            arrastendoForma = false;
        }
    }

    @Override
    public void aoClicar(MouseEvent e, EditorContext ctx) {}

    private void selecionarFormasDaRegiao(EditorContext ctx) {
        double x1 = Math.min(selecaoStartX, selecaoAtualX);
        double y1 = Math.min(selecaoStartY, selecaoAtualY);
        double x2 = Math.max(selecaoStartX, selecaoAtualX);
        double y2 = Math.max(selecaoStartY, selecaoAtualY);

        List<FormaGeometrica> selecionadas = new ArrayList<>();
        for (FormaGeometrica f : ctx.getFormas()) {
            // verifica se o centroide está dentro da região de seleção
            var c = f.getCentroide();
            if (c.getX() >= x1 && c.getX() <= x2 && c.getY() >= y1 && c.getY() <= y2) {
                selecionadas.add(f);
            }
        }
        ctx.setFormasSelecionadas(selecionadas);
        ctx.setFormaSelecionada(selecionadas.isEmpty() ? null : selecionadas.get(0));
    }
}
