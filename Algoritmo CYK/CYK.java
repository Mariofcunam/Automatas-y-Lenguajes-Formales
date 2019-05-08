/* -------------------------------------------------------------------
 * CYK.java
 * versión 1.0
 * Copyright (C) 2019  Mario Navarrete Baltazar.
 * Facultad de Ciencias,
 * Universidad Nacional Autónoma de México, Mexico.
 *
 * Este programa es software libre; se puede redistribuir
 * y/o modificar en los términos establecidos por la
 * Licencia Pública General de GNU tal como fue publicada
 * por la Free Software Foundation en la versión 2 o
 * superior.
 *
 * Este programa es distribuido con la esperanza de que
 * resulte de utilidad, pero SIN GARANTÍA ALGUNA; de hecho
 * sin la garantía implícita de COMERCIALIZACIÓN o
 * ADECUACIÓN PARA PROPÓSITOS PARTICULARES. Véase la
 * Licencia Pública General de GNU para mayores detalles.
 *
 * Con este programa se debe haber recibido una copia de la
 * Licencia Pública General de GNU, de no ser así, visite el
 * siguiente URL:
 * http://www.gnu.org/licenses/gpl.html
 * o escriba a la Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * -------------------------------------------------------------------
*/

import java.io.*;
import java.util.*;

/**
*Implementa el algoritmo CYK que usa una archivo que contiene una GLC y checa si la cadena acepta
*la cadena 
*Autor: Mario Navarrete
Fecha: Abril 2019
*/

public class CYK{

    public static String cadena;
    public static String simboloInicial;
    public static boolean esCadenaToken=false;
    public static ArrayList<String> terminales = new ArrayList<String>();
    public static ArrayList<String> noTerminales = new ArrayList<String>();
    public static TreeMap<String, ArrayList<String>> gramatica = new TreeMap<>();
    
    public static void main(String[] args){
        if(args.length<2){
            System.out.println("Uso: java CYK <archivo> <cadena>");
            System.exit(1);
        } else if(args.length>2){
            esCadenaToken=true;
        }
        realizarPasos(args);
    }
    public static void realizarPasos(String[] args){
        analizarGramatica(args);
        String[][] cykTabla= crearTablaCYK();
        imprimirResultado(realizaCYK(cykTabla));
    }
    public static void analizarGramatica(String[] args){
        Scanner input =  abrirArchivo(args[0]);
        ArrayList<String> temp = new ArrayList<>();
        int linea = 2;

        cadena = getCadena(args);
        simboloInicial=input.next();
        input.nextLine();

        while(input.hasNextLine()&&linea<=3){
            temp.addAll(Arrays.<String>asList(toArray(input.nextLine())));
            if(linea==2){terminales.addAll(temp);}
            if(linea==3){noTerminales.addAll(temp);}
            temp.clear();
            linea++;
        }
        while(input.hasNextLine()){
            temp.addAll(Arrays.<String>asList(toArray(input.nextLine())));
            String ladoIzquierdo=temp.get(0);
            temp.remove(0);
            gramatica.put(ladoIzquierdo, new ArrayList<String>());
            gramatica.get(ladoIzquierdo).addAll(temp);
            temp.clear();
        }
        input.close();
    }
    public static String getCadena(String[] args){
        if(!esCadenaToken){return args[1];}
        String[] argsSinArchivo = new String[args.length-1];
    for(int i=0; i<args.length;i++){
        argsSinArchivo[i-1] = args[i];
    }
    return toString(argsSinArchivo);
    }
    public static void imprimirResultado(String[][] cykTabla){
        System.out.println("Cadena:" + cadena);
        System.out.println("\nG=("+terminales.toString().replace("[","{").replace("]","}")
                        +", "+ noTerminales.toString().replace("[","{").replace("]","}")
                        +", P ," + simboloInicial + ")\n\nCon producciones P como:");
        for(String s:gramatica.keySet()){
            System.out.println(s + " -> " + gramatica.get(s).toString().replaceAll("[\\[\\]\\,]", "").replaceAll("\\s", " | "));
        }
        System.out.println("\nAplicando algoritmo:\n");
        dibujarTabla(cykTabla);                
    }
    public static void dibujarTabla(String[][] cykTabla){
        int l = encuentraLaCadenaMasLarga(cykTabla) + 2;
        String formato = "| %-" + l + "s ";
        String s = "";
        StringBuilder sb = new StringBuilder();
        //Crea una tabla
        sb.append("+");
        for(int x=0; x<=1+2; x++){
            if(x == 1+2){
                sb.append("+");
            } else {
                sb.append("-");
            }
        }
        String delFondo = sb.toString();
        sb.delete(0,1);
        String delFondoDerecha=sb.toString();
        //Imprimir tabla
        for(int i=0; i<cykTabla.length;i++){
            for(int j=0; j < cykTabla[i].length;j++){
                System.out.println((j==0) ? delFondo : (i<= 1 && j == cykTabla[i].length-1) ? "" : delFondoDerecha);
            }
            System.out.println();
            for(int j= 0; j < cykTabla[i].length;j++){
                if(cykTabla[i][j] == null){
                    continue;
                }
                s = (cykTabla[i][j].isEmpty()) ? "-" : cykTabla[i][j];
                System.out.format(formato, s.replaceAll("\\s", ","));
                if(j == cykTabla[i].length - 1) { System.out.print("|"); }
            }
            System.out.println();
        }
        System.out.println(delFondo+"\n");
            
        if(cykTabla[cykTabla.length-1][cykTabla[cykTabla.length-1].length-1].contains(simboloInicial)){
            System.out.println("La cadena\""+ cadena + "\" pertenece a la gramatica y puede ser derivada.");
        } else {
            System.out.println("La cadena\"" + cadena + "\" no pertenece a la gramatica y no puede ser derviada.");  
            }
        }
        public static int encuentraLaCadenaMasLarga(String[][] cykTabla){
            int x = 0;
            for(String[] s: cykTabla){
                for(String d : s){
                    if(d == null){
                        continue;
                    }
                    if(d.length() > x){x=d.length();}
                }
            }
            return x;
        }
        public static String[][] crearTablaCYK(){
            int length = esCadenaToken ? toArray(cadena).length : cadena.length();
            String[][] cykTabla = new String[length+1][];
            cykTabla[0] = new String[length];
            for(int i=1; i<cykTabla.length;i++){
                cykTabla[i] = new String[length - (i - 1)];
            }
            for(int i=1; i<cykTabla.length;i++){
                for(int j=0; j<cykTabla[i].length;j++){
                    cykTabla[i][j]="";
                }
            }
            return cykTabla;
            
        }
        public static String[][] realizaCYK(String[][] cykTabla){
            for(int i=0;i<cykTabla[0].length;i++){
                cykTabla[0][1] = manejarCadena(cadena,i);
            }
            for(int i=0; i<cykTabla[1].length;i++){
                String[] combinacionesValidas = checaSiProduce(new String[]{cykTabla[0][i]});
                cykTabla[1][i] = toString(combinacionesValidas);
            }
            if(cadena.length()<=1){return cykTabla;}
            for(int i=0; i < cykTabla[2].length;i++){
                String[] haciaAbajo = toArray(cykTabla[1][i]);
                String[] diagonal = toArray(cykTabla[1][i+1]);
                String[] combinacionesValidas = checaSiProduce(getTodasLasCombinaciones(haciaAbajo,diagonal));
                cykTabla[2][i] = toString(combinacionesValidas);
            }
            if(cadena.length()<=2){return cykTabla;}
            TreeSet<String> valoresActuales = new TreeSet<String>();
            
            for(int i=3; i<cykTabla.length;i++){
                for(int j=0; j<cykTabla[i].length;j++){
                    for(int k=1; k<i; k++){
                        String[] haciaAbajo=cykTabla[k][j].split("\\s");
                        String[] diagonal = cykTabla[i-k][j+k].split("\\s");
                        String[] combinaciones = getTodasLasCombinaciones(haciaAbajo,diagonal);
                        String[] combinacionesValidas = checaSiProduce(combinaciones);
                        if(cykTabla[i][j].isEmpty()){
                            cykTabla[i][j]=toString(combinacionesValidas);
                        } else {
                            String[] viejosValores = toArray(cykTabla[i][j]);
                            ArrayList<String>  nuevosValores = new ArrayList<String>(Arrays.asList(viejosValores));
                            nuevosValores.addAll(Arrays.asList(combinacionesValidas));
                            valoresActuales.addAll(nuevosValores);
                            cykTabla[i][j] = toString(valoresActuales.toArray(new String[valoresActuales.size()]));
                        }
                    }
                    valoresActuales.clear();
                }
            }
            return cykTabla;
        }
        public static String manejarCadena(String cadena, int posicion){
            if(!esCadenaToken){return Character.toString(cadena.charAt(posicion));}
            return toArray(cadena)[posicion];
        }
        public static String[] checaSiProduce(String[] aChecar){
            ArrayList<String> almacenado = new ArrayList<>();
            for(String s : gramatica.keySet()){
                for(String actual : aChecar){
                    if(gramatica.get(s).contains(actual)){
                        almacenado.add(s);
                    }
                }
            }
            if(almacenado.size()==0){return new String[] {}; }
            return almacenado.toArray(new String[almacenado.size()]);
        }
        public static String[] getTodasLasCombinaciones(String[] de, String[] a){
            int length = de.length * a.length;
            int contador = 0;
            String[] combinaciones = new String[length];
            if(length==0){return combinaciones; };
            for(int i=0; i<de.length;i++){
                for(int j=0; j<a.length;j++){
                    combinaciones[contador]=de[i] + a[j];
                    contador++;
                }
            }
            return combinaciones;
        }
        public static String toString(String[] entrada){
            return Arrays.toString(entrada).replaceAll("[\\[\\]\\,]", "");
        }
        public static String[] toArray(String entrada){
            return entrada.split("\\s");
        }
        public static Scanner abrirArchivo(String archivo){
            try{
                return new Scanner(new File(archivo));
            }catch(FileNotFoundException e){
                System.out.println("No se puede abrir o encontrar archivo" + archivo + ".");
                System.exit(1);
                return null;
            }
        }
    }