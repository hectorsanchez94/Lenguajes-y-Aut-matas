
public class Gramatica {
    private String  [] t;
    private String  [] n;
    private String  s;
    private String  [] p;
    
    public Gramatica(){
        t = n = p = null;
        s = "";
    }

    public void setT(String[] t) {
        this.t = t;
    }

    public void setN(String[] n) {
        this.n = n;
    }

    public void setS(String s) {
        this.s = s;
    }

    public void setP(String[] p) {
        this.p = p;
    }
    
    public String recorrido(String exp){
        String temp2 = validar(exp);
        if(temp2 == null){
            return null;
        }
        String temp = "-\t"+s+"\n";
        String []t = temp2.split("-");
        String t3 = s;
        int n;
        for(String n1 : t){
            n = Integer.parseInt(n1);
            n -= 1;
            t3 = t3.replaceFirst(p[n].charAt(0)+"", p[n].substring(2, p[n].length()));
            temp += (n+1)+"\t"+t3+"\n";
        }
        return temp;
    }
    
    public String validar(String exp){
        String av = s;
        String r = busquedaCamino(av, exp);
        return (r == null)?null:r;
    }
    
    private String busquedaCamino(String av,String exp){
        String temp, temp2;
        if(exp.length() == av.length()){
            return (!exp.equals(av))?null:"";
        }
        if(exp.length() < av.length()){
            return null;
        }
        for(int i = 0; i < av.length(); i++){
            if(!exp.substring(i, i+1).equals(av.substring(i, i+1))){
                if(esNoterm(av.substring(i, i+1))){
                    for(int y = 0; y < p.length; y++){
                        if(p[y].charAt(0) == av.charAt(i)){
                            temp2 = p[y].substring(2, p[y].length());
                            String g = av.replaceFirst(p[y].charAt(0)+"", temp2);
                            temp = busquedaCamino(g, exp);
                            if(temp != null){ return (y+1)+"-"+temp;}
                        }
                    }
                    return null;
                }
            }
        }
        return null;
    }
    
    public boolean esNoterm(String c){
        for(String n1 : n) {
            if (c.equals(n1)) {
                return true;
            }
        }
        return false;
    }
    
}
