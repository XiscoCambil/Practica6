/**
 * Created by fjcambilr on 09/05/16.
 */
public class Atom {

    public enum Type {
        CHAR, INICIO, FINAL, CHARLIST, RANGO, CLOUSURE, INTERROGANTE, ARROBA
    }

    public Type type;
    public char caracter;
    public char[] listaCaracteres;

    public Atom(){}

    public String toString(){
        return type.toString() + " " + caracter;
    }

}
