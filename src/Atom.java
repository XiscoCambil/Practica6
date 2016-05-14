import java.util.ArrayList;
import java.util.List;

/**
 * Created by fjcambilr on 09/05/16.
 */
public class Atom {

    //Distintos tipos posibles para los Atoms
    public enum Type {
        CHAR, INICIO, DOLLAR, CHARLIST, CHARLISTFINAL, INTERROGANTE, GUION, CLOUSURE
    }
    //Atributos para los objeto Atoms
    public Type type;
    public char caracter;

    public Atom(){}


}


