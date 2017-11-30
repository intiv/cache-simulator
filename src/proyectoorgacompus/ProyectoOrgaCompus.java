/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoorgacompus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author Inti Velasquez
 */
public class ProyectoOrgaCompus {

    /**
     * @param args the command line arguments
     */
    public int[] cache = new int[1024];
    public int[] ram = new int[4096];
    boolean[] valid = new boolean[128];
    boolean[] mod = new boolean[128];
    int[] etiqueta = new int[128];
    int[] bloque = new int[128];
    int[] conjunto = new int[128];
    int[][] cacheData = new int[128][8];
    double contadorTiempo= 0;
    double tiempoSinCache = 0;
    double tiempoDirecta = 0;
    double tiempoAsoc = 0;
    double tiempoConjunt = 0;
    public ProyectoOrgaCompus() {

    }

    public void config() { 
            
            int temp;
            int menor, mayor, a;
            int n = 2000;
            for (int tipo = 0; tipo < 3; tipo++) {
                cargarDatos();
                if(tipo < 2){
//                escribir(100,tipo,10);    //En la memoria 100 escribe un 10
//                escribir(101,tipo,13);
//                escribir(102,tipo,21);
//                escribir(103,tipo,11);
//                escribir(104,tipo,67);
//                escribir(105,tipo,43);
//                escribir(106,tipo,9);
//                escribir(107,tipo,11);
//                escribir(108,tipo,19);
//                escribir(109,tipo,23);
//                escribir(110,tipo,32);
//                escribir(111,tipo,54);
//                escribir(112,tipo,98);
//                escribir(113,tipo,7);
//                escribir(114,tipo,13);
//                escribir(115,tipo,1);
//                menor=leer(100,tipo);
//                mayor=menor;
//                a=0;
//                for(int i=101;i<=115;i++){
//                   a++;
//                   escribir(615,tipo,a);
//                   if (leer(i,tipo)<menor)
//                       menor=leer(i,tipo);
//                   if (leer(i,tipo)>mayor)
//                       mayor=leer(i,tipo);}
              
                
                    for (int i = 0; i < n - 1; i++) {
                        for (int j = i + 1; j < n; j++) {
                            if (leer(i, tipo) > leer(j, tipo)) {
                                temp = leer(i, tipo);
                                escribir(i, tipo, leer(j, tipo));
                                escribir(j, tipo, temp);
                            }
                        }
                    }
                }
            }
            System.out.println(this.tiempoDirecta);
            imprimirTiempos();
            
        
    }
    
    public void cargarDatos(){
        try{
            File datos = new File("./datos.txt");

            FileReader fr = new FileReader(datos);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int index = 0;
            while ((line = br.readLine()) != null) {
                ram[index]=Integer.parseInt(line);
                index++;
            }
            
            br.close();
            fr.close();
        }catch(IOException ioex){
            System.err.println("Error cargando datos "+ioex.toString());
        }
    }
    
    public int leer(int dir, int tipo) {//INDEX: posicion de la ram, TIPO: 0:   No utiliza  caché 1:   Caché directo 2:   Caché Asociativo 3:   Caché Asociativo por conjuntos
        
        try{
            if (tipo == 0) {

                this.contadorTiempo+= 0.1;//sin cache
                this.tiempoSinCache += 0.1;
                return ram[dir];

            } else if (tipo == 1) {//Correspondencia directa


                int block = Math.floorDiv(dir, 8);//direccion/k
                int linea = block%128; //bloque%m
                int bloqueInicio = block*8;
                if(!this.valid[linea]){
                    this.valid[linea] = true;
                    this.mod[linea] = false;
                    for (int k = 0; k < 8; k++) {
                        cacheData[linea][dir] = ram[bloqueInicio + k];
                    }
                    this.bloque[linea] = block;
                    this.contadorTiempo+= 0.11;
                    this.tiempoDirecta += 0.11;
                }else if(this.bloque[linea] != block){
                    if(this.mod[linea]){
                        System.arraycopy(this.cacheData[linea], 0, this.ram, bloqueInicio, 8);
                        this.valid[linea] = true;
                        this.mod[linea] = false;
                        this.contadorTiempo+= 0.22;
                        this.tiempoDirecta+=0.22;
                        for (int k = 0; k < 8; k++) {
                            this.cacheData[linea][k] = this.ram[bloqueInicio + k];
                        }
                    }else{
                        this.contadorTiempo+=0.11;
                        this.tiempoDirecta+=0.11;
                    }
                    this.bloque[linea] = block;
                }else{
                    this.contadorTiempo+=0.01;
                    this.tiempoDirecta+=0.01;
                }

                return this.cacheData[linea][dir%8];

            } else if (tipo == 2) { //Correspondencia asociativa
                int linea = -1; //bloque%m
                int binaryDir = Integer.rotateRight(dir, 3) & 4095;
                for (int j = 0; j < 128; j++) {
                    if(this.etiqueta[j] == binaryDir){
                        linea = j;
                    }
                }
                if(linea == -1){
                    boolean validLine = false;
                    Random r = new Random();
                    int index = -1;
                    while(!validLine){
                    
                        index = r.nextInt(127);
                        if(!this.valid[index]){
                            validLine=true;
                        }
                    }
                    this.valid[index] = true;
                    this.etiqueta[index] = binaryDir;
                    for (int k = 0; k < 8; k++) {
                        this.cacheData[index][k] = this.ram[dir + k];
                    }
                    this.tiempoAsoc += 0.11;
                    linea = index;
                    
                }
                return this.cacheData[linea][dir & 7];
                

            } else if (tipo == 3) {//Asociativa por conjuntos                    this.tiempoAsoc += 0.11;

                

            } else {

                return -1;

            }
        }catch(Exception ex){

        }
            
        
        return -1;
        
    }
    
    public void obtainTimes(){
        System.out.println(tiempoDirecta);
        
        this.tiempoDirecta = Math.round(this.tiempoDirecta*100.0)/100.0;
        this.tiempoAsoc = this.tiempoDirecta;
        this.tiempoConjunt = this.tiempoDirecta;
    }
    
    
    
    

    public void escribir(int dir, int tipo, int dato) {//INDEX: posicion de la ram, TIPO: 0:   No utiliza  caché 1:   Caché directo 2:   Caché Asociativo 3:   Caché Asociativo por conjuntos
        
        
        if( tipo ==0 ){//Sin cache
            ram[dir] = dato;
            this.contadorTiempo += 0.1;
            this.tiempoSinCache += 0.1;
        }if(tipo==1){//directa
            int block = Math.floorDiv(dir, 8);
            int linea = block%128;
            int bloqueInicio = block*8;
            if(!this.valid[linea]){
                this.valid[linea] = true;
                this.mod[linea] = true;
                for (int k = 0; k < 8; k++) {
                    this.cacheData[linea][k] = this.ram[bloqueInicio + k];
                }
                this.bloque[linea] = block;
                this.contadorTiempo+=0.11;
                this.tiempoDirecta+=0.11;
            }else if(this.bloque[linea] != block){
                if(this.mod[linea]){
                    System.arraycopy(this.cacheData[linea], 0, this.ram, bloqueInicio, 8);
                    
                    this.contadorTiempo+= 0.22;
                    this.tiempoDirecta+=0.22;
                    //this.contadorTiempo+=0.11;
//                  
                }else{
                    this.tiempoDirecta+=0.11;
                    this.contadorTiempo+=0.11;
                }
                this.valid[linea] = true;
                this.mod[linea] = true;
                this.bloque[linea] = block;
                for (int k = 0; k < 8; k++) {
                    this.cacheData[linea][k] = this.ram[bloqueInicio + k];
                }
                this.contadorTiempo+=0.11;
                this.tiempoDirecta+=0.11;
            }else{
                this.mod[linea] = true;
                this.contadorTiempo+=0.01;
                this.tiempoDirecta+=0.01;
            }
            this.cacheData[linea][dir%8] = dato;
        }if(tipo ==2){//Asociativo
            int linea = -1;
            int binaryDir = Integer.rotateRight(dir, 3) & 4095;
                for (int j = 0; j < 128; j++) {
                    if(this.etiqueta[j] == binaryDir){
                        linea = j;
                    }
                }
                if(linea == -1){
                    boolean validLine = false;
                    Random r = new Random();
                    int index = -1;
                    while(!validLine){
                    
                        index = r.nextInt(127);
                        if(!this.valid[index]){
                            validLine=true;
                        }
                    }
                    this.valid[index] = true;
                    this.etiqueta[index] = binaryDir;
                    for (int k = 0; k < 8; k++) {
                        this.cacheData[index][k] = this.ram[dir + k];
                    }
                    this.tiempoAsoc += 0.11;
                    
                }else{
                    for (int k = 0; k < 8; k++) {
                        this.cacheData[linea][k] = this.ram[dir + k];
                    }
                    this.tiempoAsoc+=0.11;
                }
            
        }if(tipo ==3){
            
        }else{
            
        }
        
        
    }
    
    public void imprimirTiempos(){
        obtainTimes();
        System.out.println("Contador total: "+this.contadorTiempo);
        System.out.println("Contador sin cache: "+Math.round(this.tiempoSinCache*100.0)/100.0);
        System.out.println("Contador directa: "+this.tiempoDirecta);
        System.out.println("Contador asociativa: "+this.tiempoAsoc);
        System.out.println("Contador asociativa por conjuntos: "+this.tiempoConjunt);
    }
    
    
    

    public static void main(String[] args) {
        ProyectoOrgaCompus po = new ProyectoOrgaCompus();
        po.config();
    }
    
    

}
