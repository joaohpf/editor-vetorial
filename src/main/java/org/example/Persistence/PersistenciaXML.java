package org.example.Persistence;

import org.example.Models.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PersistenciaXML {

    public static void salvar(List<FormaGeometrica> formas, File arquivo) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element root = doc.createElement("EditorVetorial");
        doc.appendChild(root);

        for (FormaGeometrica f : formas) {
            Element el = serializarForma(doc, f);
            if (el != null) root.appendChild(el);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(new DOMSource(doc), new StreamResult(arquivo));
    }

    public static List<FormaGeometrica> carregar(File arquivo) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(arquivo);
        doc.getDocumentElement().normalize();

        List<FormaGeometrica> formas = new ArrayList<>();
        NodeList nos = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < nos.getLength(); i++) {
            Node no = nos.item(i);
            if (no.getNodeType() == Node.ELEMENT_NODE) {
                FormaGeometrica f = desserializarForma((Element) no);
                if (f != null) formas.add(f);
            }
        }
        return formas;
    }

    private static Element serializarForma(Document doc, FormaGeometrica f) {
        String tipo;
        if (f instanceof Circulo) tipo = "Circulo";
        else if (f instanceof Quadrado) tipo = "Quadrado";
        else if (f instanceof Hexagono) tipo = "Hexagono";
        else if (f instanceof Poligono) tipo = "Poligono";
        else return null;

        Element el = doc.createElement(tipo);
        el.setAttribute("corPreenchimento", f.getCorPreenchimentoHex());
        el.setAttribute("corBorda", f.getCorBordaHex());
        el.setAttribute("espessuraBorda", String.valueOf(f.getEspessuraBorda()));
        if (f.getGroupId() != null) el.setAttribute("groupId", f.getGroupId());

        if (f instanceof Circulo c) {
            el.setAttribute("raio", String.valueOf(c.getRaio()));
            Element centro = doc.createElement("Centro");
            centro.setAttribute("x", String.valueOf(c.getCentro().getX()));
            centro.setAttribute("y", String.valueOf(c.getCentro().getY()));
            el.appendChild(centro);
        } else {
            for (Ponto p : f.getPontos()) {
                Element ep = doc.createElement("Ponto");
                ep.setAttribute("x", String.valueOf(p.getX()));
                ep.setAttribute("y", String.valueOf(p.getY()));
                el.appendChild(ep);
            }
        }
        return el;
    }

    private static FormaGeometrica desserializarForma(Element el) {
        try {
            String tipo = el.getTagName();
            String corP = el.getAttribute("corPreenchimento");
            String corB = el.getAttribute("corBorda");
            double esp = Double.parseDouble(el.getAttribute("espessuraBorda"));
            String gid = el.hasAttribute("groupId") ? el.getAttribute("groupId") : null;

            FormaGeometrica f = switch (tipo) {
                case "Circulo" -> {
                    Element centro = (Element) el.getElementsByTagName("Centro").item(0);
                    double cx = Double.parseDouble(centro.getAttribute("x"));
                    double cy = Double.parseDouble(centro.getAttribute("y"));
                    double raio = Double.parseDouble(el.getAttribute("raio"));
                    yield new Circulo(new Ponto(cx, cy), raio);
                }
                case "Quadrado" -> {
                    List<Ponto> pts = lerPontos(el);
                    Ponto centroide = calcularCentroide(pts);
                    double raio = distancia(pts.get(0), centroide);
                    Quadrado q = new Quadrado(centroide, raio);
                    q.getPontos().clear();
                    q.getPontos().addAll(pts);
                    yield q;
                }
                case "Hexagono" -> {
                    List<Ponto> pts = lerPontos(el);
                    Ponto centroide = calcularCentroide(pts);
                    double raio = distancia(pts.get(0), centroide);
                    Hexagono h = new Hexagono(centroide, raio);
                    h.getPontos().clear();
                    h.getPontos().addAll(pts);
                    yield h;
                }
                case "Poligono" -> {
                    Poligono p = new Poligono();
                    for (Ponto pt : lerPontos(el)) p.adicionarPonto(pt);
                    yield p;
                }
                default -> null;
            };

            if (f != null) {
                f.setCorPreenchimentoHex(corP.isEmpty() ? "#AAAAAA" : corP);
                f.setCorBordaHex(corB.isEmpty() ? "#000000" : corB);
                f.setEspessuraBorda(esp);
                f.setGroupId(gid);
            }
            return f;
        } catch (Exception e) {
            return null;
        }
    }

    private static List<Ponto> lerPontos(Element el) {
        List<Ponto> lista = new ArrayList<>();
        NodeList nos = el.getElementsByTagName("Ponto");
        for (int i = 0; i < nos.getLength(); i++) {
            Element ep = (Element) nos.item(i);
            lista.add(new Ponto(
                Double.parseDouble(ep.getAttribute("x")),
                Double.parseDouble(ep.getAttribute("y"))
            ));
        }
        return lista;
    }

    private static Ponto calcularCentroide(List<Ponto> pts) {
        double sx = 0, sy = 0;
        for (Ponto p : pts) { sx += p.getX(); sy += p.getY(); }
        return new Ponto(sx / pts.size(), sy / pts.size());
    }

    private static double distancia(Ponto a, Ponto b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
