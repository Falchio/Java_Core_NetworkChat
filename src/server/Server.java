package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    private Vector<ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        clients = new Vector<>();
        authService = new SimpleAuthService();
        ServerSocket server = null;
        Socket socket = null;

        try {
            server = new ServerSocket(8189);
            System.out.println("Сервер запущен");

            while (true){
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this,socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMsg(String msg){
        for (ClientHandler c:clients ) {
            c.sendMsg(msg);
        }
    }

    public void sendPrivateMessage(String msg, ClientHandler sender){
        String[] privateMessage = msg.split(" ", 3);
        boolean privateMsgDeliveryStatus = false;

        try{

            for (ClientHandler c:clients ) {

                if (sender.getNick().equalsIgnoreCase(privateMessage[1])){
                    sender.sendMsg("Зачем Вы пишете сами себе?");
                    privateMsgDeliveryStatus=true;
                    break; // попытка отправить сообщение самому себе
                }

                if(c.getNick().equalsIgnoreCase(privateMessage[1])){
                    c.sendMsg("Личное сообщение от " + sender.getNick() + ": " + privateMessage[2]);
                    sender.sendMsg(sender.getNick()+" для "+ c.getNick() +": " + privateMessage[2]);
                    privateMsgDeliveryStatus = true;
                }
            }

        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        if (!privateMsgDeliveryStatus){
            sender.sendMsg("Адресат не в сети.");
        }
    }



    public void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
    }
}
