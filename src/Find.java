import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xisco on 06/05/2016.
 */

public class Find {
    private String text;
    private int tamañoRango;
    private boolean RangoFallado;

    public Find(String text) {
        this.text = text;
    }

    public boolean match(String pattern) {
        ReiniciarVariables();
        if (pattern.length() == 0) {
            return false;
        }
        List<Atom> lista = ConstruirAtom(pattern);
        for (int i = 0; i < text.length(); i++) {
            if (match2(lista, i) == true) return true;
        }
        return false;
    }

    public boolean match2(List<Atom> lista, int i) {
        try {
            if (RangoFallado) {
                return false;
            }
            for (int j = 0; j < lista.size(); j++) {
                Atom a = lista.get(j);
                if (i == text.length()) {
                    if (a.type == Atom.Type.DOLLAR) {
                        return true;
                    }
                    if (a.caracter == '+') {
                        if(lista.get(j-1).type == Atom.Type.CHAR && lista.get(j-1).caracter == text.charAt(i-1)) return true;
                        if(lista.get(j-1).type == Atom.Type.CHARLISTFINAL && !RangoFallado)return true;
                    }
                    if (a.caracter == '*') {
                        return true;
                    }
                    return false;
                }
                char c = text.charAt(i);
                switch (a.type) {
                    case INTERROGANTE:
                        break;
                    case CHAR:
                        if (c != a.caracter) return false;
                        break;
                    case ARROBA:
                        if (c != a.caracter) return false;
                        break;
                    case INICIO:
                        if(lista.get(j+1).type == Atom.Type.CHARLIST ){
                            if (!ComprobarRango(lista, i, j)) {
                                RangoFallado = true;
                                return false;
                            }
                            j += tamañoRango;
                            tamañoRango = 0;
                            break;
                        }
                        if (i > 0) return false;
                        j++;
                        break;
                    case CHARLIST:
                        if (!ComprobarRango(lista, i, j)) {
                            RangoFallado = true;
                            return false;
                        }
                        j += tamañoRango;
                        tamañoRango = 0;
                        break;
                    case DOLLAR:
                        return false;
                    case CLOUSURE:
                        i--;
                        char caracterComparar = text.charAt(i);
                        if (a.caracter == '+') {
                            if (lista.get(j - 1).type == Atom.Type.CHAR && caracterComparar == lista.get(j - 1).caracter) {
                                while (text.charAt(i) == caracterComparar) {
                                    if(i == text.length()-1)return true;
                                    i++;
                                }

                            }
                        }
                        i--;
                        break;
                }
                i++;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public List<Atom> ConstruirAtom(String pattern) {
        List<Atom> lista = new ArrayList<>();
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '?') {
                lista.add(AñadirAtom(c, pattern, i, Atom.Type.INTERROGANTE));
            } else if (c == '@') {
                lista.add(AñadirAtom(c, pattern, i, Atom.Type.CHAR));
                i++;
            } else if (c == '%') {
                lista.add(AñadirAtom(c, pattern, i, Atom.Type.INICIO));
            } else if (c == '$') {
                if (i == pattern.length() - 1) lista.add(AñadirAtom(c, pattern, i, Atom.Type.DOLLAR));
                else lista.add(AñadirAtom(c, pattern, i, Atom.Type.CHAR));
            } else if (c == '[') {
                lista.add(AñadirAtom(c, pattern, i, Atom.Type.CHARLIST));
            } else if (c == ']') {
                lista.add(AñadirAtom(c, pattern, i, Atom.Type.CHARLISTFINAL));
            } else if (c == '+') {
                lista.add(AñadirAtom(c, pattern, i, Atom.Type.CLOUSURE));
            } else if (c == '*') {
                lista.add(AñadirAtom(c, pattern, i, Atom.Type.CLOUSURE));
            } else if (c == '-') {
                lista.add(AñadirAtom(c, pattern, i, Atom.Type.GUION));
            } else {
                lista.add(AñadirAtom(c, pattern, i, Atom.Type.CHAR));
            }
        }
        return lista;
    }

    public boolean ComprobarRango(List<Atom> listaRango, int c, int j) {
        j++;
        tamañoRango = 1;
        boolean resultado = false;
        while (listaRango.get(j).type != Atom.Type.CHARLISTFINAL) {
            if (listaRango.get(j).type == Atom.Type.GUION) {
                if (text.charAt(c) > listaRango.get(j - 1).caracter && text.charAt(c) < listaRango.get(j + 1).caracter)
                    resultado = true;
            } else if (listaRango.get(j).type == Atom.Type.CHAR) {
                if (listaRango.get(j + 1).type != Atom.Type.GUION && listaRango.get(j - 1).type != Atom.Type.GUION && text.charAt(c) == listaRango.get(j).caracter)
                    resultado = true;
            }
            j++;
            tamañoRango++;
        }
        return resultado;
    }

    public Atom AñadirAtom(char c, String pattern, int i, Atom.Type type) {
        Atom a = new Atom();
        if (c == '@') {
            a.type = type;
            a.caracter = pattern.charAt(i + 1);
        } else if (type == Atom.Type.CLOUSURE) {
            a.type = type;
            a.caracter = c;
        } else {
            a.type = type;
            a.caracter = c;
        }
        return a;
    }

    public void ReiniciarVariables() {
        RangoFallado = false;
        tamañoRango = 0;
    }

}