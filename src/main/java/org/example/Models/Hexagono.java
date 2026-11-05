package org.example.Models;

public class Hexagono extends FormaBase {

    public Hexagono(Ponto centro, double raio) {
        // 6 vértices regulares em incrementos de 60°
        for (int i = 0; i < 6; i++) {
            double angulo = i * Math.PI / 3.0;
            pontos.add(new Ponto(
                centro.getX() + raio * Math.cos(angulo),
                centro.getY() + raio * Math.sin(angulo)
            ));
        }
    }

    // Construtor interno para clonar
    private Hexagono() {}

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
        Hexagono copia = new Hexagono();
        copia.pontos = clonarPontos();
        copiarAtributos(copia);
        return copia;
    }

    @Override
    public String toString() {
        return "Hexagono[pontos=" + pontos.size() + "]";
    }
}
