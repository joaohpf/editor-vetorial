package org.example.Models;

import java.util.ArrayList;
import java.util.List;

public abstract class FormaBase implements FormaGeometrica {
    protected List<Ponto> pontos = new ArrayList<>();
    protected String corPreenchimentoHex = "#AAAAAA";
    protected String corBordaHex = "#000000";
    protected double espessuraBorda = 1.0;
    protected String groupId = null;

    @Override public void adicionarPonto(Ponto p) { pontos.add(p); }
    @Override public List<Ponto> getPontos() { return pontos; }
    @Override public int getQuantidadePontos() { return pontos.size(); }

    @Override public String getCorPreenchimentoHex() { return corPreenchimentoHex; }
    @Override public void setCorPreenchimentoHex(String hex) { this.corPreenchimentoHex = hex; }

    @Override public String getCorBordaHex() { return corBordaHex; }
    @Override public void setCorBordaHex(String hex) { this.corBordaHex = hex; }

    @Override public double getEspessuraBorda() { return espessuraBorda; }
    @Override public void setEspessuraBorda(double espessura) { this.espessuraBorda = espessura; }

    @Override public String getGroupId() { return groupId; }
    @Override public void setGroupId(String groupId) { this.groupId = groupId; }

    @Override
    public Ponto getCentroide() {
        if (pontos.isEmpty()) return new Ponto(0, 0);
        double sx = 0, sy = 0;
        for (Ponto p : pontos) { sx += p.getX(); sy += p.getY(); }
        return new Ponto(sx / pontos.size(), sy / pontos.size());
    }

    @Override
    public void transladar(double dx, double dy) {
        for (Ponto p : pontos) p.mover(dx, dy);
    }

    @Override
    public void escalar(double fator) {
        Ponto c = getCentroide();
        for (Ponto p : pontos) {
            p.setX(c.getX() + (p.getX() - c.getX()) * fator);
            p.setY(c.getY() + (p.getY() - c.getY()) * fator);
        }
    }

    @Override
    public void rotacionar(double anguloRadianos) {
        Ponto c = getCentroide();
        double cos = Math.cos(anguloRadianos);
        double sin = Math.sin(anguloRadianos);
        for (Ponto p : pontos) {
            double dx = p.getX() - c.getX();
            double dy = p.getY() - c.getY();
            p.setX(c.getX() + dx * cos - dy * sin);
            p.setY(c.getY() + dx * sin + dy * cos);
        }
    }

    @Override
    public void cisalhar(double shx, double shy) {
        for (Ponto p : pontos) {
            double nx = p.getX() + shx * p.getY();
            double ny = p.getY() + shy * p.getX();
            p.setX(nx);
            p.setY(ny);
        }
    }

    @Override
    public double calcularArea() {
        int n = pontos.size();
        if (n < 3) return 0;
        double area = 0;
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            area += pontos.get(i).getX() * pontos.get(j).getY();
            area -= pontos.get(j).getX() * pontos.get(i).getY();
        }
        return Math.abs(area) / 2.0;
    }

    @Override
    public double calcularPerimetro() {
        int n = pontos.size();
        if (n < 2) return 0;
        double perim = 0;
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            double dx = pontos.get(j).getX() - pontos.get(i).getX();
            double dy = pontos.get(j).getY() - pontos.get(i).getY();
            perim += Math.sqrt(dx * dx + dy * dy);
        }
        return perim;
    }

    protected List<Ponto> clonarPontos() {
        List<Ponto> copia = new ArrayList<>();
        for (Ponto p : pontos) copia.add(p.clonar());
        return copia;
    }

    protected void copiarAtributos(FormaBase destino) {
        destino.corPreenchimentoHex = this.corPreenchimentoHex;
        destino.corBordaHex = this.corBordaHex;
        destino.espessuraBorda = this.espessuraBorda;
        destino.groupId = this.groupId;
    }
}
