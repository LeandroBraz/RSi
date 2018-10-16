/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import javax.swing.JOptionPane;

/**
 *
 * @author Leandro
 */
public class Avisos {
    
    
    public Avisos(){}
    
    
       public void portaEmUso(){
    
        JOptionPane.showMessageDialog(null, "Porta já está em uso\n escolha outra porta","Porta em uso", JOptionPane.INFORMATION_MESSAGE);
    }
    
}
