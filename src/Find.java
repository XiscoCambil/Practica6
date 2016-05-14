import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xisco on 06/05/2016.
 */

public class Find {
    private final String text;
    private int tamañoRango;
    private boolean RangoFallado;

    public Find(String text) {
        this.text = text;
    }

    // Funcion que devulve el resultado final de la comprobacion
    public boolean match(String pattern) {
        ReiniciarVariables();
        if (pattern.length() == 0) {
            return false;
        }
        List<Atom> lista = ConstruirAtom(pattern);
        for (int i = 0; i < text.length(); i++) {
            if (match2(lista, i)) return true;
        }
        return false;
    }

    //Metodo utilizado para la comprobacion del match
    private boolean match2(List<Atom> lista, int i) {
        try {
            //Evitamos la entrada si el rango a fallado
            if (RangoFallado) {
                return false;
            }
            for (int j = 0; j < lista.size(); j++) {
                Atom a = lista.get(j);
                //Control del final de patern en caso de que el texto llegue a su fin.
                if (i == text.length()) {
                    return ControlFinal(a, lista, j, i);
                }
                char c = text.charAt(i);
                switch (a.type) {
                    case INTERROGANTE:
                        break;
                    case CHAR:
                        char caracterComparar = text.charAt(i);
                        //Controlamos si nos encontramos un clousure al final.
                        if (j < lista.size() - 1 && lista.get(j + 1).caracter == '*') {
                            if (lista.get(j).caracter != text.charAt(i)) {
                                //Restamos a texto en caso de no a ver concidencia.
                                i--;
                            } else {
                                //Aumentamos posicion de i en caso de acierto.
                                while (text.charAt(i) == caracterComparar) {
                                    if (i == text.length() - 1) return true;
                                    i++;
                                }
                                continue;
                            }
                            j++;
                            break;
                        }
                        if (c != a.caracter) return false;
                        break;
                    case INICIO:
                        //Comprobamos si hay un rango al inicio
                        if (lista.get(j + 1).type == Atom.Type.CHARLIST) {
                            if (!ComprobarRango(lista, i, j)) {
                                RangoFallado = true;
                                return false;
                            }
                            j += tamañoRango;
                            tamañoRango = 0;
                            break;
                        }
                        //En caso contrario comprobamos si i esta al inicio
                        if (i > 0) return false;
                        j++;
                        break;
                    case CHARLIST:
                        //Comprobacion del rango
                        if (!ComprobarRango(lista, i, j)) {
                            RangoFallado = true;
                            return false;
                        }
                        j += tamañoRango;
                        tamañoRango = 0;
                        break;
                    case DOLLAR:
                        //Comprobacion del caso dollar, solo se entrara en esta opcion en algunos casos especiales.
                        return lista.get(j - 1).caracter == text.charAt(text.length() - 1);
                    case CLOUSURE:
                        //Control del clousure +
                        if (a.caracter == '+') {
                            caracterComparar = text.charAt(i - 1);
                            if (lista.get(j - 1).type == Atom.Type.CHAR && caracterComparar == lista.get(j - 1).caracter) {
                                while (text.charAt(i) == caracterComparar) {
                                    if (i == text.length() - 1) return true;
                                    i++;
                                }

                            }
                        }
                        //Control del clousure *
                        if (a.caracter == '*') {
                            if (lista.get(j - 1).type == Atom.Type.CHARLISTFINAL) {
                                j++;
                            }
                        }
                        continue;
                }
                i++;
            }
            //Control de las posibles excepciones
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //Metodo ConstruirAtom para añadir los atoms a la lista
    private List<Atom> ConstruirAtom(String pattern) {
        List<Atom> lista = new ArrayList<>();
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '?') {
                lista.add(AñadirAtom(c, pattern, i, Atom.Type.INTERROGANTE));
            } else if (c == '@') {
                i++;
                c = pattern.charAt(i);
                lista.add(AñadirAtom(c, pattern, i, Atom.Type.CHAR));
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

    //Metodo para comrpobar los distintos rangos.
    private boolean ComprobarRango(List<Atom> listaRango, int c, int j) {
        j++;
        tamañoRango = 1;
        boolean resultado = false;
        while (listaRango.get(j).type != Atom.Type.CHARLISTFINAL) {
            if (listaRango.get(j).type == Atom.Type.GUION) {
                //Rangos [a-z] o [a-zA-z]
                if (text.charAt(c) > listaRango.get(j - 1).caracter && text.charAt(c) < listaRango.get(j + 1).caracter)
                    resultado = true;
                //Rangos [a-zn] o [asdf]
            } else if (listaRango.get(j).type == Atom.Type.CHAR) {
                if (listaRango.get(j + 1).type != Atom.Type.GUION && listaRango.get(j - 1).type != Atom.Type.GUION && text.charAt(c) == listaRango.get(j).caracter)
                    resultado = true;
            }
            j++;
            //Controlamos si hay un clousure de tipo * despues del rango
            if (j < listaRango.size() - 1 && listaRango.get(j + 1).caracter == '*') {
                tamañoRango++;
                return true;
            }
            tamañoRango++;
        }
        return resultado;
    }

    //Metodo para clasificar los atoms.
    private Atom AñadirAtom(char c, String pattern, int i, Atom.Type type) {
        Atom a = new Atom();
        a.type = type;
        a.caracter = c;
        return a;
    }

    //Metodo para reiniciar las variables de clase
    private void ReiniciarVariables() {
        RangoFallado = false;
        tamañoRango = 0;
    }

    //Metodo para controlar los posibles finales.
    private boolean ControlFinal(Atom a, List<Atom> lista, int j, int i) {
        if (a.type == Atom.Type.DOLLAR) {
            return true;
        }
        if (a.caracter == '+') {
            if (lista.get(j - 1).type == Atom.Type.CHAR && lista.get(j - 1).caracter == text.charAt(i - 1))
                return true;
            if (lista.get(j - 1).type == Atom.Type.CHARLISTFINAL && !RangoFallado) return true;
        }
        return a.caracter == '*';
    }
}