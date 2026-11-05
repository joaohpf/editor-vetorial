package org.example.Controller;

import javafx.scene.input.MouseEvent;

public interface Ferramenta {
    void aoPressionarMouse(MouseEvent e, EditorContext ctx);
    void aoArrastarMouse(MouseEvent e, EditorContext ctx);
    void aoSoltarMouse(MouseEvent e, EditorContext ctx);
    void aoClicar(MouseEvent e, EditorContext ctx);
    default void aoMoverMouse(MouseEvent e, EditorContext ctx) {}
}
