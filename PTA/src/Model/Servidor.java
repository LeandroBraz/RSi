/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author leandro
 */
public class Servidor {
    
    public ServerSocket serverSocket;
    int porta;
    
    public Servidor(int porta) throws IOException{
        
        this.porta = porta;
        serverSocket = new ServerSocket(porta);
    
    }
    
    public Socket retornar() throws IOException{
        
        
    
        return serverSocket.accept();
    }

   
    
    
    
}
