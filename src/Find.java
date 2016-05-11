import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xisco on 06/05/2016.
 */

public class Find {
    private String text;
    private boolean hayDolar;
    private int tamañoRango = 0;
    private boolean hayGuion;
    private int nGuiones;
    private boolean RangoFallado = false;

    public Find(String text) {
        this.text = text;
    }

    public boolean match(String pattern) {
        if (pattern.length() == 0) {
            return false;
        }
        List<Atom> lista = ConstruirAtom(pattern);
        hayDolar = Dollar(lista);
        for (int i = 0; i < text.length(); i++) {
            if (match2(lista, i) == true) return true;
        }
        return false;
    }

    public boolean match2(List<Atom> lista, int i) {
        try {
            for (int j = 0; j < lista.size(); j++) {
                Atom a = lista.get(j);
                char c = text.charAt(i);
                if (j == lista.size() - 2 && hayDolar) {
                    if (i != text.length() - 1) return false;
                    if (i == text.length() - 1 && a.caracter != c) return false;
                    return true;
                }
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
                        Atom b = lista.get(j + 1);
                        if (b.caracter != text.charAt(0)) return false;
                        j++;
                        break;
                    case DOLLAR:
                        if (i < lista.size() - 1 && j < text.length() - 1 && a.caracter != c) return false;
                        break;
                    case CHARLIST:
                        List<Atom> listaRango = new ArrayList<>();
                        listaRango = SacarRangos(lista, j);
                        boolean RangoCoincide = ComprobarRango(listaRango, i);
                        if(RangoFallado){return false;}
                        if (!RangoCoincide){RangoFallado = true; return false;}
                        j += tamañoRango;
                        break;
                    case SUM:
                        char caracterRepetido = text.charAt(i-1);
                        if(caracterRepetido != lista.get(j-1).caracter && lista.get(j-1).caracter != ']')return false;
                        else{
                            if(i == text.length()-1){
                               i--;
                            }
                        }
                }
                i++;
            }
        } catch (java.lang.StringIndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    public List<Atom> ConstruirAtom(String pattern) {
        List<Atom> lista = new ArrayList<>();
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '?') {
                Atom a = new Atom();
                a.type = Atom.Type.INTERROGANTE;
                a.caracter = c;
                lista.add(a);
            } else if (c == '@') {
                Atom a = new Atom();
                a.type = Atom.Type.CHAR;
                a.caracter = pattern.charAt(i + 1);
                lista.add(a);
                i++;
            } else if (c == '%') {
                Atom a = new Atom();
                a.type = Atom.Type.INICIO;
                a.caracter = c;
                lista.add(a);
            } else if (c == '$') {
                Atom a = new Atom();
                a.type = Atom.Type.DOLLAR;
                a.caracter = c;
                lista.add(a);
            } else if (c == '[') {
                Atom a = new Atom();
                a.type = Atom.Type.CHARLIST;
                a.caracter = c;
                lista.add(a);
            } else if (c == ']') {
                Atom a = new Atom();
                a.type = Atom.Type.CHARLISTFINAL;
                a.caracter = c;
                lista.add(a);
            }else if (c == '+') {
                Atom a = new Atom();
                a.type = Atom.Type.SUM;
                a.caracter = c;
                lista.add(a);
            } else {
                Atom a = new Atom();
                a.type = Atom.Type.CHAR;
                a.caracter = c;
                lista.add(a);
            }
        }
        return lista;
    }

    public boolean Dollar(List<Atom> lista) {
        for (int i = 0; i < lista.size(); i++) {
            Atom a = lista.get(i);
            if (a.caracter == '$' && i == lista.size() - 1) return true;
        }

        return false;
    }

    public List<Atom> SacarRangos(List<Atom> list, int j) {
        List<Atom> listaRango = new ArrayList<>();
        while (list.get(j).type != Atom.Type.CHARLISTFINAL) {
            listaRango.add(AñadirAtomRango(list, list.get(j).caracter));
            if (list.get(j).caracter == '-') nGuiones++;
            tamañoRango++;
            j++;
        }
        listaRango.add(AñadirAtomRango(list, list.get(j).caracter));
        return listaRango;
    }

    public Atom AñadirAtomRango(List<Atom> list, char c) {
        Atom a = new Atom();
        switch (c) {
            case '-':
                a.type = Atom.Type.GUION;
                a.caracter = c;
                hayGuion = true;
                break;
            case '[':
                a.type = Atom.Type.CHARLIST;
                a.caracter = c;
                break;
            case ']':
                a.type = Atom.Type.CHARLISTFINAL;
                a.caracter = c;
                break;
            default:
                a.type = Atom.Type.CHAR;
                a.caracter = c;
                break;
        }
        return a;
    }

    public boolean ComprobarRango(List<Atom> listaRango, int c) {
        int coincidencia = 0;
        boolean resultado = false;
        if (!hayGuion) {
            for (int i = 0; i < listaRango.size(); i++) {
                Atom a = listaRango.get(i);
                if (a.caracter == text.charAt(c)) coincidencia++;
            }
            if (coincidencia == 0) return false;
        } else {
            List<Atom> Posiciones = new ArrayList<>();
            for (int i = 0; i < listaRango.size(); i++) {
                Atom a = listaRango.get(i);
                if (a.type == Atom.Type.CHAR) Posiciones.add(a);
                if (a.type == Atom.Type.CHARLIST) Posiciones.add(a);
                if (a.type == Atom.Type.CHARLISTFINAL) Posiciones.add(a);
                if (a.type == Atom.Type.GUION) {
                    Atom b = listaRango.get(i + 2);
                    // Si hay un rango y una letra exacta
                    if (b.type == Atom.Type.CHAR && i == listaRango.size() - 3) {
                        Posiciones.add(listaRango.get(i + 1));
                        Posiciones.add(listaRango.get(i + 2));
                        Atom r = new Atom();
                        if (text.charAt(c) > Posiciones.get(0).caracter && text.charAt(c) < Posiciones.get(1).caracter || text.charAt(c) == Posiciones.get(4).caracter) {
                            return true;
                        }
                        return false;
                    } else if (b.type == Atom.Type.CHARLISTFINAL) {
                        Posiciones.add(listaRango.get(i + 1));
                        Posiciones.add(listaRango.get(i + 2));
                        if (text.charAt(c) > Posiciones.get(1).caracter && text.charAt(c) < Posiciones.get(2).caracter) {
                            return true;
                        }
                        return false;
                    } else {
                        for (int j = 0, k = 3 ; j < nGuiones; j++,k++) {
                            if(j == 0){Posiciones.add(listaRango.get(k)); continue;}
                            Posiciones.add(listaRango.get(k));
                            Posiciones.add(listaRango.get(k+2));
                            k+=2;
                        }
                        Posiciones.add(listaRango.get(listaRango.size()-1));
                        for (int j = 1; j < Posiciones.size(); j++) {
                            if (text.charAt(c) > Posiciones.get(j).caracter && text.charAt(c) < Posiciones.get(j+1).caracter)
                                return true;
                        }
                        return false;
                    }

                }

            }
        }
    return true;

    }

}
//public class Find {
//    private String text;
//
//
//    public Find(String text) {
//        this.text = text;
//    }
//
//    //Devolucion del match correcto o erroneo
//    public boolean match(String pattern) {
//        if (pattern.length() == 0) {
//            return false;
//        }
//        for (int i = 0; i < text.length(); i++) {
//            if (match2(text, pattern, i) == true) return true;
//        }
//        return false;
//    }
//
//    //Comprobacion de la validez del match
//    public boolean match2(String text, String pattern, int i) {
//        for (int j = 0; j < pattern.length(); j++) {
//            String caracteres = "";
//            try {
//                if (pattern.charAt(j) == '@') {
//                    if (text.charAt(i) != pattern.charAt(j + 1)) return false;
//                    j += 2;
//                    i++;
//                } else if(pattern.charAt(j) == '+'){
//
//                } else if (pattern.charAt(j) == '?') {
//                    if (!ComprobarInterrogante(text, pattern, i, j)) return false;
//                } else if (pattern.charAt(j) == '%') {
//                    if (!ComprobarInicio(text, pattern, j,i,caracteres)) return false;
//                    j++;
//                } else if (pattern.charAt(j) == '$' || (j == pattern.length() - 2 && pattern.charAt(j + 1) == '$')) {
//                    if (!ComprobarDollar(text, pattern, i, j)) return false;
//                    else {
//                        if (i == text.length() - 1) {
//                            break;
//                        }
//                    }
//                } else if (pattern.charAt(j) == '[') {
//                    boolean rangoActivado = false;
//                    j++;
//                    while (pattern.charAt(j) != ']') {
//                        if (pattern.charAt(j) == '-') rangoActivado = true;
//                        caracteres += pattern.charAt(j);
//                        j++;
//                    }
//                    if (rangoActivado) {
//                        if (!Co if (text.charAt(i) != pattern.charAt(j + 1)) return false;
//                    j += 2;
//                    i++;mprobarRango(text, i, caracteres)) return false;
//                    } else {
//                        int contador = 0;
//                        for (int k = 0; k < caracteres.length(); k++) {
//                            if (text.charAt(i) == caracteres.charAt(k)) contador++;
//                        }
//                        if (contador == 0) return false;
//                    }text,
//                } else {
//                    if (text.charAt(i) != pattern.charAt(j)) {
//                        return false;
//                    }
//                }
//                i++;
//            } catch (java.lang.StringIndexOutOfBoundsException e) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public boolean ComprobarInterrogante(String text, String pattern, int i, int j) {
//        while (pattern.charAt(j) == '?') {
//            if (i == text.length() - 1 && j == pattern.length() - 1) {
//                return true;
//            }
//            j++;
//            i++;
//        }
//        if (pattern.charAt(j) == text.charAt(i)) {
//            return true;
//        } else return false;
//    }
//
//    public boolean ComprobarInicio(String text, String pattern, int j, int i, String caracteres) {
//        if (pattern.charAt(j + 1) != text.charAt(0)) {
//            return false;
//        }
//        return true;
//    }
//
//    public boolean ComprobarDollar(String text, String pattern, int i, int j) {
//        if (j == pattern.length() - 2 && pattern.charAt(j + 1) == '$' && i == text.length() - 1 && pattern.charAt(j) == text.charAt(i)) {
//            return true;
//        } else if (pattern.charAt(j) == '$' && i < text.length() - 1 && pattern.charAt(j) == text.charAt(i)) {
//            return true;
//        } else {
//
//            return false;
//        }
//    }
//
//    public boolean ComprobarRango(String text, int i, String caracteres) {
//        boolean resultado = false;
//        String rango = "";
//        for (int k = 0; k < caracteres.length(); k++) {
//            if (caracteres.charAt(k) != '-') rango += caracteres.charAt(k);
//            else {
//                k++;
//                rango += caracteres.charAt(k);
//                if (caracteres.length() == 4) {
//                    k++;
//                    rango += caracteres.charAt(k);
//                    if (text.charAt(i) > rango.charAt(0) && text.charAt(i) < rango.charAt(1) || text.charAt(i) == rango.charAt(2))
//                        resultado = true;
//                    else resultado = false;
//                } else if (caracteres.length() == 6) {
//                    while (k < caracteres.length() - 1) {
//                        k++;
//                        if (caracteres.charAt(k) != '-') {
//                            rango += caracteres.charAt(k);
//                        } else {
//                            k++;
//                            rango += caracteres.charAt(k);
//                            if ((text.charAt(i) > rango.charAt(0) && text.charAt(i) < rango.charAt(1)) || (text.charAt(i) > rango.charAt(2) && text.charAt(i) < rango.charAt(3)))
//                                resultado = true;
//                            else resultado = false;
//                        }
//                    }
//                } else {
//                    if (text.charAt(i) > rango.charAt(0) && text.charAt(i) < rango.charAt(1)) return true;
//                    else resultado = false;
//                }
//            }
//        }
//        return resultado;
//    }
//
//}