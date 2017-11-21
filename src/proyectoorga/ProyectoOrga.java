/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoorga;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Inti Velasquez
 */
public class ProyectoOrga {

    /**
     * @param args the command line arguments
     */
    public int[] cache = new int[1024];
    public int[] ram = new int[4096];
    boolean[] valid = new boolean[128];
    boolean[] mod = new boolean[128];
    int[] etiqueta = new int[128];
    int[][] cacheConjuntos = new int[128][8];
    double contadorTiempo= 0;
    int contador=0;

    public ProyectoOrga() {

    }

    public void config() { 
        try {
            File datos = new File("./datos.txt");

            FileReader fr = new FileReader(datos);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int asd = 0;
            while ((line = br.readLine()) != null) {
                ram[asd]=Integer.parseInt(line);
                asd++;
            }
            int temp;
            for (int tipo = 0; tipo < 4; tipo++) {
                int n = 4096;
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
        } catch (IOException ioex) {
            System.err.println("Error leyendo datos "+ioex.toString());
        }
        leer(1,0);
        System.out.println(this.contadorTiempo);
        System.out.println(this.contador);
    }

    public int leer(int i, int tipo) {//INDEX: posicion de la ram, TIPO: 0:   No utiliza  caché 1:   Caché directo 2:   Caché Asociativo 3:   Caché Asociativo por conjuntos
        
        if (i >= 0 && i <= 4095) {
            if (tipo == 0) {
                this.contador++;
                this.contadorTiempo+= 0.1;//sin cache
            } else if (tipo == 1) {
                this.contadorTiempo+= 0.01;//lee o escribe de la cache (ESTA EN CACHE)
            } else if (tipo == 2) {
                this.contadorTiempo+= 0.11;//lee de la cache pero antes hay que passarlo de RAM -> Cache (NO ESTA EN CACHE)
            } else if (tipo == 3) {
                this.contadorTiempo+= 0.22;//lee de la cache pero antes hay que passarlo de RAM -> Cache, Cache -> RAM (SE HA MODIFICADO LA LINEA)
            } else {
                return -1;
            }
            
        }
        return 0;
    }

    public void escribir(int index, int tipo, int dato) {//INDEX: posicion de la ram, TIPO: 0:   No utiliza  caché 1:   Caché directo 2:   Caché Asociativo 3:   Caché Asociativo por conjuntos
        this.ram[index] = dato;
        
        if( tipo ==0 ){
            this.contadorTiempo += 0.1;
            
        }if(tipo==1){
            
        }if(tipo ==2){
            
        }if(tipo ==3){
            
        }else{
            
        }
        
        
    }

    public static void main(String[] args) {
        ProyectoOrga po = new ProyectoOrga();
        po.config();
    }

}
