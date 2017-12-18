
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author IVAN
 */
public class AnalizadorLexico {
    String t = "";
    Hashtable<String, Simbolo> tablaSimbolos;
    String Alfabeto=("[\\w]*[\\x09]*[\\x20]+[\\x3C]*[\\x3E]*[\\x3D]*[\\x2B]*[\\x2D]*[\\x2F]*[\\x2A]*[\\x3A]*[\\x2C]*[\\x28]*[\\x29]*[\\x5B]*[\\x5D]*[\\x7B]*[\\x7D]*[ñ]*[Ñ]*[á]*[Á]*[é]*[É]*[í]*[Í]*[ó]*[Ó]*[ú]*[Ú]*[\\x7C]*[\\x26]*[\\x25]*[\\x5F]*[\\x5E]*[\\x23]*[\\x3F]*[\\x23]*[\\x22]*[\\x2E]*[\\x20]+");
    String Alfabeto2=("[\\w]*[\\x09]*[\\x20]*[\\x3C]*[\\x3E]*[\\x3D]*[\\x2B]*[\\x2D]*[\\x2F]*[\\x2A]*[\\x3A]*[\\x2C]*[\\x28]*[\\x29]*[\\x5B]*[\\x5D]*[\\x7B]*[\\x7D]*[ñ]*[Ñ]*[á]*[Á]*[é]*[É]*[í]*[Í]*[ó]*[Ó]*[ú]*[Ú]*[\\x7C]*[\\x26]*[\\x25]*[\\x5F]*[\\x5E]*[\\x3F]*[\\xF9]*[\\x2E]*");
    String patron = ("(Dx\\b|start\\b|end\\b|table\\b|infinity\\b|pi\\b|euler\\b|if\\b|else\\b|during\\b|from\\b|to\\b|do\\b|terminal\\b|expression\\b|thread\\b|main\\b|convertion\\b|call\\b|in\\b|out\\b|graphic\\b)|"
            +"(natural\\b|integer\\b|real\\b|function\\b|text\\b|bit\\b)|"//TIPO_DATO
            + "([:][:]|<=|>=|<|>|[=][?])|" //operador relacional
            + "([-][=]|[+][=]|[/][=]|[*][=]|[=])|" //operador de asignacion
            + "([a-zA-Z]+[a-zA-Z_0-9]*)|" //identificador
            + "([\\x22]["+Alfabeto2+"]*[\\x22][#]["+Alfabeto2+"]+[#][\\x22]["+Alfabeto2+"]+[\\x22]|[\\x22]["+Alfabeto2+"]*[\\x22][#]["+Alfabeto2+"]+)|" //Concatenacion
            + "([\\x22][\\x22])|" //Cadena Vacia
            + "([*][*]["+Alfabeto+"]+)|" //Comentario Simple
            + "([{][["+Alfabeto+"]+[\\x0A]*]+[}])|" //Comentario Largo
            + "([\\x22]["+Alfabeto2+"]+[\\x22])|" //Cadenas
            + "([#]|[\\x22]|[*][*]|[{]|[}])|" //signos especiales
            + "(AND|OR)|" //operador logico
            + "(\\x28|\\x29|\\x5B|\\x5D)|"//signos de agrupacion
            +"([\\x2B|\\x2D]{0,1}[\\d]+[.][\\d]+|\\d+|[\\x2B|\\x2D]{0,1}[\\x2E]{0,1}[\\d]+)|"//numero
            + "([-|/|^|+|*])|"//operador aritmetico
            + "([.|,|;])");//signos de puntuacion
    String[] tokens ={"<Palabra Reservada, %s>\n", "<Tipo de Dato, %s>\n", "<Operador Relacional, %s>\n", "<Operador de Asignación, %s>\n",
                     "<Identificador, %s>\n", "<Concatenación, %s>\n", "<Cadena Vacia, %s>\n", "<Comentario Simple, %s>\n",
                     "<Comentario Largo, %s>\n", "<Cadena de Texto, %s>\n", "<Función Especial, %s>\n", "<Operador Lógico, %s>\n",
                     "<Signo de Agrupación, %s>\n", "<Número, %s>\n", "<Operador Aritmetico, %s>\n","<Signo de Puntuación, %s>\n"};
        
    private String [] re;
    
    public String [] compilar(String codigo){
        t = "";
        tablaSimbolos = new Hashtable<>();
        re = new String[2];
        re[0] = re[1] = "";
        String []texto = codigo.split("\n");
        String w;
        String stop = "\":=()[],+-*/<>^; ";
        
        for(int y = 0;y < texto.length; y++){
            w = "";
            for(int x = 0;x < texto[y].length(); x++){
                w += texto[y].substring(x,x+1);
                if(x < texto[y].length()-1){
                    if(stop.substring(8,14).contains(w) || stop.substring(1,3).contains(w)){
                        if(x < texto[y].length()-1){
                            if((w.equals("+") || w.equals("-")) && lexDig()){
                                boolean b = false;
                                String dig="0123456789";
                                for(x++;x<= texto[y].length();x++){
                                    if(x == texto[y].length()){
                                        break;
                                    }
                                    if(!texto[y].substring(x,x+1).equals(" ") && !b){
                                        if(dig.contains(texto[y].substring(x,x+1))){
                                            w += texto[y].substring(x,x+1);
                                        }else{
                                            x--;
                                            break;
                                        }
                                    }
                                }      
                                compLex(w, y+1);
                                w="";  
                            }else if(texto[y].substring(x+1,x+2).equals("=")){// si es +=,-=,/=,*=,>=,<=
                                    w += texto[y].substring(x+1,x+2);
                                    x++;         
                                    compLex(w, y+1);
                                    w="";                       
                            }else if(w.equals(":") && texto[y].substring(x+1,x+2).equals(":")){//si es ::
                                    w += texto[y].substring(x+1,x+2);
                                    x++;         
                                    compLex(w, y+1);
                                    w="";                                
                            }else if(w.equals("=") && texto[y].substring(x+1,x+2).equals("?")){// si es =?
                                    w += texto[y].substring(x+1,x+2);
                                    x++;         
                                    compLex(w, y+1);
                                    w="";                                
                            }
                        }
                    }else if(stop.contains(texto[y].substring(x+1,x+2))){//separador
                        if(w.equals("\"")){
                            for(x++;x<= texto[y].length();x++){
                                if(x == texto[y].length()){
                                    re[1]+="Error en la línea "+(y+1)+". "+"\" pérdido.\n";
                                    w = "";
                                    break;
                                }
                                w += texto[y].substring(x,x+1);
                                if(texto[y].substring(x,x+1).equals("\"")){
                                    break;
                                }
                            }
                        }
                        compLex(w, y+1);
                        w="";
                    }
                }
                if(stop.contains(w) && !w.equals("")){//si es cadena de texto o cadena vacia
                    if(w.equals("\"")){
                        for(x++;x<= texto[y].length();x++){
                            if(x == texto[y].length()){
                                re[1]+="Error en la línea "+(y+1)+". "+"\" pérdido.\n";
                                w = "";
                                break;
                            }
                            w += texto[y].substring(x,x+1);
                            if(texto[y].substring(x,x+1).equals("\"")){
                                break;
                            }
                        }
                    }
                    compLex(w, y+1);
                    w="";
                }
            }
            compLex(w, y+1);
            w="";
            t +="\n";
        }
        return re;
    }
    
    public boolean lexDig(){
        String [] ar = re[0].split("\n");
        if(ar.length==1){
            if(ar[0].equals("")){
                return true;
            }else if(ar[ar.length-1].substring(1,ar[ar.length-1].indexOf(",")).equals("Número")){
                return false;
            }
            return true;
        }
        if(ar[ar.length-1].substring(1,ar[ar.length-1].indexOf(",")).equals("Número")){
            return false;
        }
        return true;
    }
    
    Simbolo simbolo = new Simbolo();
    String nombre = "";
    boolean bandera = false; 
    
    private void compLex(String temp, int l){
        if (temp.isEmpty() || temp.equals(" ")) {
            return;
        }
        Pattern p = Pattern.compile(patron);
        Matcher matcher = p.matcher(temp);
        String r = null;
        int c = 0;
        //Ciclo while para que encuentre todas las coincidencias, y la que tenga mas caracteres sera la mas probable
        while(matcher.find()){
            for (int i =1; i <= tokens.length;i++) {
                if(matcher.group(i) != null){
                    if(r == null){
                        r = matcher.group(i);
                        c=i-1;
                    }
                    if(r.length() < matcher.group(i).length()){
                        r = matcher.group(i);
                        c = i-1;
                    }else if(temp.length() == r.length()){ 
                        tabla(i, temp, l); // Enviar los datos para realizar la inserción en la tabla de simbolos
                        re[0] += tokens[i-1];
                        re[0] = re[0].replace("%s", temp);
                        
                        switch(tokens[i-1]){
                            case "<Número, %s>\n":{
                                t += "NU@";
                                break;
                            }
                            case "<Cadena de Texto, %s>\n":{
                                t += "CADENA@";
                                break;
                            }
                            case "<Cadena Vacia, %s>\n":{
                                t += "CAD_VACIA@";
                                break;
                            }
                            case "<Identificador, %s>\n":{
                                t += "ID@";
                                break;
                            }
                            case "<Tipo de Dato, %s>\n":{
                                t += "TIPO_DATO@";
                                break;
                            }
                            default:{
                                t += temp+"@";
                            }
                        }
                        return;
                    }
                }
            }
        }
        re[1] += "Error léxico en la línea "+l+". "+tokens[c].substring(1,tokens[c].indexOf(","))+": "+temp+", "
        +temp.replace(r, " ")+" encontrado.\n";        //se reemplaza lo que esta bien y lo demas lo pondra en error
    }
    private void tabla(int i, String temp, int l){ 
        if (i == 2) {// verificar si el grupo es de los tipos de datos
            simbolo.tipo= temp;//Le asignamos el tipo de dato
        }
        if (i == 5) {//Verificar si el grupo corresponde a un identificador                
            if (bandera) {
                simbolo.valor=temp;
                simbolo.fila=l;
            }else{
                nombre = temp;//Asignamos el identificador a nombre
                simbolo.fila = l;//Asignamos la fila en la que fue declarado el identificador
            }
        }
        if (i == 4) {//Verificar si el grupo corresponde al de un operador de asignación
            if (temp.equals("=")) {//Verificar si el operador de asignación es el igual
                bandera = true;//Activamos la bandera que nos permitira asignar el valor
            }
        }
        if ((i == 7 || i == 10) && bandera) {//Verificar si el grupo corresponde a una cadena vacia o a una cadena de texto, además verificar si corresponde a un valor
            simbolo.valor = temp;//Asignamos el valor 
            bandera = false;//Desactivamos la bandera
        }
        if (i == 14 && bandera) {//Verificar si el grupo es un numero y si la bandera esta activada para considerarla como el valor del idenficador
            simbolo.valor = temp;//Asignamos el valor al identificador
            bandera = false;//Desactivar la bandera
        }
        if(i == 16) {//Verificar si el grupo corresponde a un signo de puntuación
            if (temp.equals(";") || temp.equals(",")) {//Verificar si el signo de puntuación corresponde con el delimitador de linea
                if (!tablaSimbolos.containsKey(nombre) && simbolo.tipo!=null && simbolo.fila!=0 && !nombre.equals("")) {//Verificamos que la tabla de simbolos no contenga el identificador y verificar que realmente se esta declarando la variable y no modificando
                    tablaSimbolos.put(nombre, new Simbolo(simbolo.tipo, simbolo.valor, simbolo.fila));//Insertamos en la tabla un nuevo simbolo
                    nombre = "";
                    simbolo = new Simbolo();//inicializamos la variable simbolo para que este disponible para recibir nuevos valores
                }else{//En este caso, significa que el identificador ya existe en la tabla de simbolos
                    if (simbolo.tipo==null && !nombre.equals("") && simbolo.valor!=null) {//Verificamos si el identificador que se intenta insertar, se esta declarando o bien si se pretende modificar el valor de éste
                        if (tablaSimbolos.containsKey(nombre)) {
                           tablaSimbolos.get(nombre).valor= simbolo.valor;//Buscamos el identificador en la tabla de simbolos y actualizamos su valor
                            nombre = "";
                            simbolo =new Simbolo();//Inicializamos la variable simbolo para que este disponible para almacenar nuevo valores para los identificadores
                        }
                    }else{
                        //Error por identificador duplicado
                        //AreaErrores.setText("Error, en la linea "+simbolo.fila+" Identificador duplicado");
                    }                    
                    nombre="";// Inicializar la variable para el nombre del identificador
                    simbolo = new Simbolo();// Inicializar la variable simbolo
                }
            }
        }
    }
}
