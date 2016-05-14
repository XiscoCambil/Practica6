import java.util.ArrayList;
import java.util.List;

/**
 * Created by fjcambilr on 09/05/16.
 */
public class Atom {

    public enum Type {
        CHAR, INICIO, DOLLAR, CHARLIST, CHARLISTFINAL, INTERROGANTE, GUION, CLOUSURE
    }



    public Type type;
    public char caracter;
    public List<String> listaCaracteres = new ArrayList<>();

    public Atom(){}

    public String toString(){
        return type.toString() + " " + caracter;
    }

}


