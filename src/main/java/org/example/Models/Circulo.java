package org.example.Models;

import java.util.List;

public class Circulo extends FormaBase {
    private double raio;

    public Circulo(Ponto centro, double raio) {
        if (raio <= 0) throw new IllegalArgumentException("Raio deve ser estritamente positivo (r > 0)");
        this.pontos.add(centro.clonar());
        this.raio = raio;
    }

    public double getRaio() { return raio; }

    public void setRaio(double raio) {
        if (raio <= 0) throw new IllegalArgumentException("Raio deve ser estritamente positivo (r > 0)");
        this.raio = raio;
    }

    public Ponto getCentro() {
        return pontos.isEmpty() ? new Ponto(0, 0) : pontos.get(0);
    }

    @Override
    public Ponto getCentroide() { return getCentro(); }

    @Override
    public void escalar(double fator) {
        raio = Math.max(0.1, raio * fator);
    }

    @Override
    public void rotacionar(double anguloRadianos) {
        // círculo é invariante à rotação em torno do próprio centro
    }

    @Override
    public void cisalhar(double shx, double shy) {
        // cisalhamento transforma círculo em elipse; aplica no centro
        Ponto c = getCentro();
        double nx = c.getX() + shx * c.getY();
        double ny = c.getY() + shy * c.getX();
        c.setX(nx);
        c.setY(ny);
    }

    @Override
    public double calcularArea() {
        return Math.PI * raio * raio;
    }

    @Override
    public double calcularPerimetro() {
        return 2 * Math.PI * raio;
    }

    @Override
    public boolean contemPonto(double px, double py) {
        Ponto c = getCentro();
        double dx = px - c.getX();
        double dy = py - c.getY();
        return Math.sqrt(dx * dx + dy * dy) <= raio;
    }

    @Override
    public void adicionarPonto(Ponto p) {
        if (pontos.isEmpty()) pontos.add(p);
        // círculo só tem um ponto (centro)
    }

    @Override
    public FormaGeometrica clonar() {
        Circulo copia = new Circulo(getCentro(), raio);
        copiarAtributos(copia);
        return copia;
    }

    @Override
    public String toString() {
        return String.format("Circulo[centro=%s, raio=%.2f]", getCentro(), raio);
    }
}
