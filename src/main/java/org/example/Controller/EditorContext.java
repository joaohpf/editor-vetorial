package org.example.Controller;

import org.example.Models.FormaGeometrica;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditorContext {
    private List<FormaGeometrica> formas = new ArrayList<>();
    private FormaGeometrica formaSelecionada = null;
    private List<FormaGeometrica> formasSelecionadas = new ArrayList<>();
    private Ferramenta ferramentaAtual;
    private boolean modoSnapping = false;
    private double tamanhoGrid = 20.0;
    private String corPreenchimentoHex = "#AAAAAA";
    private String corBordaHex = "#000000";
    private double espessuraBorda = 1.0;
    private boolean modificado = false;

    private final HistoricoController historico = new HistoricoController();
    private Runnable requestPaintCallback;

    // Getters e setters
    public List<FormaGeometrica> getFormas() { return formas; }
    public void setFormas(List<FormaGeometrica> formas) { this.formas = formas; }

    public FormaGeometrica getFormaSelecionada() { return formaSelecionada; }
    public void setFormaSelecionada(FormaGeometrica f) { this.formaSelecionada = f; }

    public List<FormaGeometrica> getFormasSelecionadas() { return formasSelecionadas; }
    public void setFormasSelecionadas(List<FormaGeometrica> fs) { this.formasSelecionadas = fs; }

    public Ferramenta getFerramentaAtual() { return ferramentaAtual; }
    public void setFerramentaAtual(Ferramenta f) { this.ferramentaAtual = f; }

    public boolean isModoSnapping() { return modoSnapping; }
    public void setModoSnapping(boolean v) { this.modoSnapping = v; }

    public double getTamanhoGrid() { return tamanhoGrid; }
    public void setTamanhoGrid(double v) { this.tamanhoGrid = v; }

    public String getCorPreenchimentoHex() { return corPreenchimentoHex; }
    public void setCorPreenchimentoHex(String hex) { this.corPreenchimentoHex = hex; }

    public String getCorBordaHex() { return corBordaHex; }
    public void setCorBordaHex(String hex) { this.corBordaHex = hex; }

    public double getEspessuraBorda() { return espessuraBorda; }
    public void setEspessuraBorda(double v) { this.espessuraBorda = v; }

    public boolean isModificado() { return modificado; }
    public void setModificado(boolean v) { this.modificado = v; }

    public HistoricoController getHistorico() { return historico; }

    public void setRequestPaintCallback(Runnable r) { this.requestPaintCallback = r; }
    public void requestPaint() { if (requestPaintCallback != null) requestPaintCallback.run(); }

    public void adicionarForma(FormaGeometrica f) {
        historico.registrarEstado(formas);
        formas.add(f);
        modificado = true;
        requestPaint();
    }

    public void removerForma(FormaGeometrica f) {
        historico.registrarEstado(formas);
        formas.remove(f);
        if (formaSelecionada == f) formaSelecionada = null;
        formasSelecionadas.remove(f);
        modificado = true;
        requestPaint();
    }

    public void removerSelecionadas() {
        if (formasSelecionadas.isEmpty() && formaSelecionada == null) return;
        historico.registrarEstado(formas);
        formas.removeAll(formasSelecionadas);
        if (formaSelecionada != null) formas.remove(formaSelecionada);
        formaSelecionada = null;
        formasSelecionadas.clear();
        modificado = true;
        requestPaint();
    }

    public void selecionarForma(FormaGeometrica f) {
        formaSelecionada = f;
        formasSelecionadas.clear();
        if (f != null) formasSelecionadas.add(f);
        requestPaint();
    }

    public void limparSelecao() {
        formaSelecionada = null;
        formasSelecionadas.clear();
        requestPaint();
    }

    public double aplicarSnap(double valor) {
        if (!modoSnapping) return valor;
        return Math.round(valor / tamanhoGrid) * tamanhoGrid;
    }

    public void agruparSelecionadas() {
        if (formasSelecionadas.size() < 2) return;
        historico.registrarEstado(formas);
        String gid = UUID.randomUUID().toString().substring(0, 8);
        for (FormaGeometrica f : formasSelecionadas) f.setGroupId(gid);
        modificado = true;
        requestPaint();
    }

    public void desagrupar() {
        if (formaSelecionada == null || formaSelecionada.getGroupId() == null) return;
        historico.registrarEstado(formas);
        String gid = formaSelecionada.getGroupId();
        for (FormaGeometrica f : formas) {
            if (gid.equals(f.getGroupId())) f.setGroupId(null);
        }
        modificado = true;
        requestPaint();
    }

    public void moverParaFrente(FormaGeometrica f) {
        if (f == null) return;
        historico.registrarEstado(formas);
        formas.remove(f);
        formas.add(f);
        modificado = true;
        requestPaint();
    }

    public void enviarParaTras(FormaGeometrica f) {
        if (f == null) return;
        historico.registrarEstado(formas);
        formas.remove(f);
        formas.add(0, f);
        modificado = true;
        requestPaint();
    }

    public void desfazer() {
        List<FormaGeometrica> estado = historico.desfazer(formas);
        if (estado != null) {
            formas = estado;
            formaSelecionada = null;
            formasSelecionadas.clear();
            modificado = true;
            requestPaint();
        }
    }

    public void refazer() {
        List<FormaGeometrica> estado = historico.refazer(formas);
        if (estado != null) {
            formas = estado;
            formaSelecionada = null;
            formasSelecionadas.clear();
            modificado = true;
            requestPaint();
        }
    }

    public FormaGeometrica encontrarFormaNoPonto(double x, double y) {
        for (int i = formas.size() - 1; i >= 0; i--) {
            if (formas.get(i).contemPonto(x, y)) return formas.get(i);
        }
        return null;
    }
}
