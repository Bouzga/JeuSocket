package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class ServerJeu extends Thread {
    private boolean isActive=true;
    private int nombreClient=0;
    private int nombreSecret;
    private boolean fin;
    private String gagnant;
    public static void main(String[]args) {
        new ServerJeu().start();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket=new ServerSocket(1234);
            nombreSecret=new Random().nextInt(1000);
            System.out.println("Le serveur a choisi son secret:" +nombreSecret);
            while(isActive){
                Socket socket=serverSocket.accept();
                ++nombreClient;
                new Conversation(socket,nombreClient).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    class Conversation extends Thread{
        private Socket socketClient;
        private int numero;
        public Conversation(Socket socketClient,int numero){
               this.socketClient=socketClient;
               this.numero=numero;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream=socketClient.getInputStream();
                InputStreamReader isr=new InputStreamReader(inputStream);
                BufferedReader br=new BufferedReader(isr);

                PrintWriter pw=new PrintWriter(socketClient.getOutputStream(),true);
                String ipClient= socketClient.getRemoteSocketAddress().toString();
                pw.println("Bienvenue vous etes le client numero:"+numero);
                System.out.println("Connexion du client numero:"+numero+",IP="+ipClient);
                pw.println("Devinez le nombre secret...?");

                while (true){
                    String req=br.readLine();
                    int nombre=0;
                    boolean correctRequest=true;
                    try {
                        nombre=Integer.parseInt(req);
                        correctRequest=true;
                    }catch (NumberFormatException e){
                        correctRequest=false;
                    }
                    if(correctRequest){
                        System.out.println("Client"+ipClient+"Tentative avec le nombre "+nombre);
                        if(fin==false){
                            if(nombre>nombreSecret){
                                pw.println("votre nombre est superieur au nombre secret");
                            }
                            else if(nombre<nombreSecret){
                                pw.println("votre nombre est inferieure au nombre secret");
                            }
                            else{
                                pw.println("Bravo vous avez gagné");
                                gagnant=ipClient;
                                System.out.println("Bravo au gagnat,Ip Client est:"+gagnant);
                                fin=true;
                            }
                        }else{
                            pw.println("Jeu est terminé, le gagnant est:"+gagnant);
                        }
                    }
                    else{
                        pw.println("Format de nombre incorrect");
                    }

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
