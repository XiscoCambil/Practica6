import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xisco on 06/05/2016.
 */

public class Find{
    private String text;


    public Find(String text) {
        this.text = text;
    }

    public boolean match(String pattern){
        if (pattern.length() == 0){
            return false;
        }
        List<Atom> lista = new ArrayList<>();
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if  (c == '?'){
                Atom a = new Atom();
                a.type = Atom.Type.INTERROGANTE;
                lista.add(a);
            }else if(c == '@') {
                Atom a = new Atom();
                a.type = Atom.Type.CHAR;
                a.caracter = pattern.charAt(i + 1);
                lista.add(a);
                i++;
            }else if(){


            } else{
                Atom a = new Atom();
                a.type = Atom.Type.CHAR;
                a.caracter = c;
                lista.add(a);
            }
        }
        for (int i = 0; i < text.length(); i++) {
           if (match2( lista, i) == true) return true;
        }
        return false;
    }

    public boolean match2(List<Atom> lista,int i){
        try{
            for (int j = 0; j < lista.size(); j++) {
                Atom a = lista.get(j);
                char c = text.charAt(i);
                switch(a.type){
                    case INTERROGANTE:
                    break;
                    case CHAR:
                        if(c != a.caracter) return false;
                        break;
                    case ARROBA:
                        if(c != a.caracter) return false;
                        break;
                }
                i++;
            }
        }catch (java.lang.StringIndexOutOfBoundsException e){
            return false;
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
//                    }
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