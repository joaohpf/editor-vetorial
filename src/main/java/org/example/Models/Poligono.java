package org.example.Models;

public class Poligono extends FormaBase {

    public Poligono() {}

    @Override
    public boolean contemPonto(double px, double py) {
        int n = pontos.size();
        if (n < 3) return false;
        boolean dentro = false;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = pontos.get(i).getX(), yi = pontos.get(i).getY();
            double xj = pontos.get(j).getX(), yj = pontos.get(j).getY();
            if (((yi > py) != (yj > py)) && (px < (xj - xi) * (py - yi) / (yj - yi) + xi))
                dentro = !dentro;
        }
        return dentro;
    }

    @Override
    public FormaGeometrica clonar() {
        Poligono copia = new Poligono();
        copia.pontos = clonarPontos();
        copiarAtributos(copia);
        return copia;
    }

    @Override
    public String toString() {
        return "Poligono[pontos=" + pontos.size() + "]";
    }
}
