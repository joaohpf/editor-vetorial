package org.example.Controller;

import org.example.Models.FormaGeometrica;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class HistoricoController {
    private final Deque<List<FormaGeometrica>> undoStack = new ArrayDeque<>();
    private final Deque<List<FormaGeometrica>> redoStack = new ArrayDeque<>();
    private static final int MAX_HISTORICO = 50;

    public void registrarEstado(List<FormaGeometrica> formas) {
        undoStack.push(clonarLista(formas));
        redoStack.clear();
        if (undoStack.size() > MAX_HISTORICO) {
            List<List<FormaGeometrica>> temp = new ArrayList<>(undoStack);
            temp.remove(temp.size() - 1);
            undoStack.clear();
            for (int i = temp.size() - 1; i >= 0; i--) undoStack.push(temp.get(i));
        }
    }

    public List<FormaGeometrica> desfazer(List<FormaGeometrica> estadoAtual) {
        if (undoStack.isEmpty()) return null;
        redoStack.push(clonarLista(estadoAtual));
        return clonarLista(undoStack.pop());
    }

    public List<FormaGeometrica> refazer(List<FormaGeometrica> estadoAtual) {
        if (redoStack.isEmpty()) return null;
        undoStack.push(clonarLista(estadoAtual));
        return clonarLista(redoStack.pop());
    }

    public boolean podeDesfazer() { return !undoStack.isEmpty(); }
    public boolean podeRefazer() { return !redoStack.isEmpty(); }

    public List<FormaGeometrica> retornar(List<FormaGeometrica> formas) {
        return clonarLista(formas);
    }

    private List<FormaGeometrica> clonarLista(List<FormaGeometrica> lista) {
        List<FormaGeometrica> copia = new ArrayList<>();
        for (FormaGeometrica f : lista) copia.add(f.clonar());
        return copia;
    }
}
