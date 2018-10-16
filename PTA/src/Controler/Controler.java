/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controler;

import Model.Servidor;
import View.Avisos;
import View.TelaServidor;
import View.TelaServiço;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leandro
 */
public class Controler implements ActionListener{
    
    Servidor servidor;
    TelaServidor telaServidor;
    TelaServiço telaServiço;
    Avisos avisos;
    String pasta = "root"; 
    OuvirPorta ouvir;
    int porta;
    
    public Controler(TelaServidor telaServidor){
    
        this.telaServidor = telaServidor;
        telaServidor.getIniciarButton().addActionListener(this);
        avisos = new Avisos();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource()== telaServidor.getIniciarButton()){
           
            try {
                porta = Integer.parseInt(telaServidor.getPortaField().getText());
                servidor = new Servidor(porta);
                telaServidor.setVisible(false);
                telaServiço = new TelaServiço();
                ouvir = new OuvirPorta();
                new Thread(ouvir).start();
                
            } catch (IOException ex) {
                avisos.portaEmUso();
            }
            
        }
                
        
    }

    public class OuvirPorta implements Runnable{
        

        @Override
        public void run() {
        
            while(true){
                Socket socket;
                try {
                    socket = servidor.retornar();
//                    servidor.retornar();
                    Request request = new Request(socket);
                    new Thread(request).start();
                } catch (IOException ex) {
                    Logger.getLogger(Controler.class.getName()).log(Level.SEVERE, null, ex);
                }
            
            }
            
            
        }
    }
        
    public class Request implements Runnable{
        
            BufferedReader in;
            String[] vetorString;
            public boolean conexao=false;
            String requisicao ="";
            String caminho ="";
            String Protocol ="";
            OutputStream  out;
            Socket socket;
            int totalenviado;
            ArrayList<String> linhas;
            SimpleDateFormat formatador;
            String header = "";
            private File f; 
            public Request(Socket socket) {
                this.socket = socket;
                try {
                    System.out.println("Entrei aq no Resquest");
                    this.linhas = new ArrayList<>();
                    formatador = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
                    formatador.setTimeZone(TimeZone.getTimeZone("GMT"));
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = socket.getOutputStream();

                            
                } catch (IOException ex) {
                    Logger.getLogger(Controler.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
     
            @Override
            public void run() {   
            synchronized (this) {
                try {
                    int contador = 0;
                    int tamanho=0;
                    requisicao = in.readLine();
                    TelaServiço.MENSAGEM_REQUISICAO += requisicao + "\n";
                    vetorString = requisicao.split(" ");
                    
                    requisicao = vetorString[0];
                    caminho = vetorString[1];
                    Protocol = vetorString[2];
                   
                    String linha = null;
                    while (!(linha = in.readLine()).isEmpty()) {
                        TelaServiço.bytesRecebidos = linha.getBytes();
                        tamanho += TelaServiço.bytesRecebidos.length;
                        
                        telaServiço.getLabelBytesRecebidos().setText("Quantidade de bytes recebidos: " + tamanho);
                        linhas.add(linha);
                        telaServiço.getRequisicoesTextArea().setText(TelaServiço.MENSAGEM_REQUISICAO);
                        contador++;
                    }
                    buscarArquivo(caminho);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
                
            }
    
     public void enviarRespota(byte[] bytes){
     String status = Protocol + " 200 OK\r\n";
            
              SimpleDateFormat formatador = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
              formatador.setTimeZone(TimeZone.getTimeZone("GMT"));
              Date data = new Date();
              
              String dataFormatada = formatador.format(data) + " GMT";
              
              String header = status
                    + "Location: http://localhost:8000/\r\n"
                    + "Date: " + dataFormatada + "\r\n"
                    + "Server: MeuServidor/1.0\r\n"
                    + "Content-Type: text/html\r\n"
                    + "Content-Length: " + bytes.length + "\r\n"
                    + "Connection: close\r\n"
                    + "\r\n";
     
                try {
                    out.write(header.getBytes());
                    if(bytes != null){
                    out.write(bytes);
                    }
                    out.flush();
                } catch (IOException ex) {
                    Logger.getLogger(Controler.class.getName()).log(Level.SEVERE, null, ex);
                }
              
     }       
            
            
    public void buscarArquivo(String caminho){
        FileInputStream file = null;
        String filename = pasta+caminho;
        if (caminho.equals("/favicon.ico")) {
                caminho = "/src/source/favicon.ico";
            }
        try {
            f = new File(filename); 
            file = new FileInputStream(f);
             byte [] conteudo = pegarBytes(f);
                enviarRespota(conteudo);
                out.flush();
                            
            } catch (FileNotFoundException ex) {  
                ex.printStackTrace();
            try {
                if(f.exists()){
                enviarRespota(listarArquivos(f).getBytes());
                out.write(listarArquivos(f).getBytes());
                out.flush();
                }
            } catch (IOException ex1) {
                Logger.getLogger(Controler.class.getName()).log(Level.SEVERE, null, ex1);
            }
              
            }    
            catch (IOException ex) {
                    Logger.getLogger(Controler.class.getName()).log(Level.SEVERE, null, ex);
            }
       
   
                     
    }
    
     public String listarArquivos(File file) {

            String html = "";
            html = "<!DOCTYPE HTML PUBLIC " + "\"" + "-//W3C//DTD HTML 3.2 Final//EN" + "\"" + "> \r\n"
                    + "<html>\r\n"
                    + "<head>\r\n"
                    + "<title>Lista de /</title>\r\n"
                    + "</head>\r\n"
                    + "<body>\r\n"
                    + "<h1>Lista de /</h1>\r\n"
                    + "<table>\r\n"
                    + " <tr><th valign=" + "\"" + "top" + "\"" + " alt=" + "\"" + "[ICO]" + "\"" + "></th><th>Name</th><th>Last modified</th><th>Size</th><th>Description</th></tr>\r\n"
                    + " <tr><th colspan=" + "\"" + "5" + "\"" + "><hr></th></tr>\r\n";
            File arquivos[] = file.listFiles();
            int i;
            for (i = 0; i < arquivos.length; i++) {
                System.out.println("\n\n\n" + arquivos[i].getName() + "\n\n\n");
                html += "<tr><td valign=" + "\"" + "top" + "\"" + "alt=" + "\"" + "[   ]" + "\"" + "></td><td><a href=" + "\"" + arquivos[i].getName() + "\"" + ">" + arquivos[i].getName() + "</a></td><td align=" + "\"" + "right" + "\">" + formatador.format(new Date(arquivos[i].lastModified())) + " </td><td align=" + "\"" + "right" + "\"> " + arquivos[i].length() + "K </td><td>&nbsp;</td></tr>\r\n"
                        + "<tr><th colspan=" + "\"" + "5" + "\"" + "><hr></th></tr>\r\n";
            }       
            for (String linha : this.linhas) {
                if (linha.split(" ")[0].equals("User-Agent:")) {
                    if (linha.split(" ")[2].equals("(Windows")) {
                        html += "</table><address> SI HTTP Server/0.0.1 "
                                + "(" + linha.split(" ")[2] + " " + linha.split(" ")[6]
                                + socket.getInetAddress()
                                + "Porta " + linhas.get(0).split(":")[2]
                                + "</address></body></html>\r\n";
                    } else {
                        html += "</table><address>  HTTP Server/0.0.1 "
                                + "(" + linha.split(" ")[3] + " " + linha.split(" ")[4]
                                + socket.getInetAddress()
                                + "Porta " + linhas.get(0).split(":")[2]
                                + "</address></body></html>\r\n";

                    }
                }
            }
            return html;

        }
     
   public byte[] pegarBytes(File file) throws IOException {
        
        long length = file.length();

        
        if (length > Integer.MAX_VALUE) {
           
            throw new IOException("File is too large!");
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;

        InputStream is = new FileInputStream(file);
        try {
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        return bytes;
    }
    
    
    }
    
    
    
    

    
}

