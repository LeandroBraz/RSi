/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App;

import Controler.Controler;
import View.TelaServidor;
import java.io.File;
import javax.swing.UIManager;

/**
 *
 * @author Leandro
 */
public class AplicaÁ„o {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String tema_padrao = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
        try {  
              UIManager.setLookAndFeel(tema_padrao);  
            } catch (Exception e) {  
        }
        
        TelaServidor telaServidor = new TelaServidor();
        telaServidor.setVisible(true);
        File diretorio = new File("root"); 
                if (!diretorio.exists()) {
                diretorio.mkdirs(); 
                } else {
                System.out.println("Diret√≥rio j√° existente");
                }
        Controler controler = new Controler(telaServidor);
        
    }
    
}
