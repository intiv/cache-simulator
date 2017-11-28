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
    public ProyectoOrgaCompus() {

    }

    public void config() { 
        int m = 16;
        System.out.println(Integer.toBinaryString(m));

        // returns the value obtained by rotating right
        for(int i = 0; i < 4; i++) {
           m = Integer.rotateRight(m, 3);
           System.out.println(m);
        }
        try {
            File datos = new File("./datos.txt");

            FileReader fr = new FileReader(datos);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int index = 0;
            while ((line = br.readLine()) != null) {
                ram[index]=Integer.parseInt(line);
                index++;
            }
            int temp;
            int n = 4096;
            for (int tipo = 0; tipo < 4; tipo++) {
                
                for (int i = 0; i <= n - 2; i++) {
                    for (int j = i + 1; j <= n - 1; j++) {
                        if (leer(i, tipo) > leer(j, tipo)) {
                            temp = leer(i, tipo);
                            escribir(i, tipo, leer(j, tipo));
                            escribir(j, tipo, temp);
                        }
                    }
                }
            }
            System.out.println("Contador total: "+this.contadorTiempo);
            System.out.println("Contador sin cache: "+this.tiempoSinCache);
            System.out.println("Contador directa: "+this.tiempoDirecta);
        } catch (IOException ioex) {
            System.err.println("Error leyendo datos "+ioex.toString());
        }
        
    }

    public int leer(int i, int tipo) {//INDEX: posicion de la ram, TIPO: 0:   No utiliza  caché 1:   Caché directo 2:   Caché Asociativo 3:   Caché Asociativo por conjuntos
        
            try{
            if (tipo == 0) {
                
                this.contadorTiempo+= 0.1;//sin cache
                this.tiempoSinCache += 0.1;
                return ram[i];
                
            } else if (tipo == 1) {//Correspondencia directa
                
                
                int block = Math.floorDiv(i, 8);//direccion/k
                int linea = block%128; //bloque%m
                int bloqueInicio = block*8;
                if(!this.valid[i]){
                    this.valid[i] = true;
                    this.mod[i] = false;
                    for (int k = 0; k < 8; k++) {
                        cacheData[linea][i] = ram[bloqueInicio + k];
                    }
                    this.bloque[linea] = block;
                    this.contadorTiempo+= 0.11;
                    this.tiempoDirecta += 0.11;
                }else if(this.bloque[linea] != block){
                    if(this.mod[linea]){
                        for (int k = 0; k < 8; k++) {
                            this.ram[bloqueInicio + k] = this.cacheData[linea][k];
                        }
                        this.valid[linea] = true;
                        this.mod[linea] = false;
                        this.contadorTiempo+= 0.11;
                        this.tiempoDirecta+=0.11;
                        //this.contadorTiempo+=0.11;
//                        for (int k = 0; k < 8; k++) {
//                            this.cacheData[linea][k] = this.ram[bloqueInicio + k];
//                        }
                    }else{
                        this.contadorTiempo+=0.11;
                        this.tiempoDirecta+=0.11;
                    }
                    this.bloque[linea] = block;
                }else{
                    this.contadorTiempo+=0.01;
                    this.tiempoDirecta+=0.01;
                }
                
                return this.cacheData[linea][i%8];
                
            } else if (tipo == 2) { //Correspondencia asociativa
                
                
            
            } else if (tipo == 3) {
                
                this.contadorTiempo+= 0.22;//lee de la cache pero antes hay que passarlo de RAM -> Cache, Cache -> RAM (SE HA MODIFICADO LA LINEA)
                
            } else {
                
                return -1;
            
            }
            }catch(Exception ex){
        
            }
            
        
        return this.ram[i];
        
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
                    for (int k = 0; k < 8; k++) {
                        this.ram[bloqueInicio + k] = this.cacheData[linea][k];
                    }
                    this.valid[linea] = true;
                    this.mod[linea] = false;
                    this.contadorTiempo+= 0.11;
                    this.tiempoDirecta+=0.11;
                    //this.contadorTiempo+=0.11;
//                  for (int k = 0; k < 8; k++) {
//                      this.cacheData[linea][k] = this.ram[bloqueInicio + k];
//                  }
                }else{
                    this.tiempoDirecta+=0.11;
                    this.contadorTiempo+=0.11;
                }
                this.bloque[linea] = block;
            }
            this.cacheData[linea][dir%8] = dato;
        }if(tipo ==2){
            
        }if(tipo ==3){
            
        }else{
            
        }
        
        
    }

    public static void main(String[] args) {
        ProyectoOrgaCompus po = new ProyectoOrgaCompus();
        po.config();
    }

}
