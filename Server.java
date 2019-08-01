package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.javarush.task.task30.task3008.ConsoleHelper.readInt;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    private static class Handler extends Thread {
        private Socket socket;

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message answer = connection.receive();
                if (answer.getType() != MessageType.USER_NAME) continue;
                String userName = answer.getData();
                if (userName == null || userName.isEmpty()) continue;
                if (connectionMap.containsKey(userName)) continue;
                connectionMap.put(userName, connection);
                connection.send(new Message(MessageType.NAME_ACCEPTED));
                return userName;
            }
        }

        private void notifyUsers(Connection connection, String userName) throws IOException {
            for (String addedUser : connectionMap.keySet()) {
                Message newUserAlert = new Message(MessageType.USER_ADDED, addedUser);
                if (addedUser != userName) {
                    connection.send(newUserAlert);
                }
            }
        }


        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message text = connection.receive();
                if (text.getType() != MessageType.TEXT) {
                    ConsoleHelper.writeMessage("There is no text message");
                } else if (text.getType() == MessageType.TEXT) {
                    Message formatedText = new Message(MessageType.TEXT, userName + ": " + text.getData());
                    sendBroadcastMessage(formatedText);
                }
            }
        }

        public void run() {
            ConsoleHelper.writeMessage("Установлено новое соединение с удаленным адресом " + socket.getRemoteSocketAddress());
            Connection connection = null;
            String userName = null;
            try {
                connection = new Connection(socket);
                userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                notifyUsers(connection, userName);
                serverMainLoop(connection, userName);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (userName != null) {
                    connectionMap.remove(userName);
                    sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
                }
            }
        }


        Handler(Socket socket) {
            this.socket = socket;
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(readInt());
        System.out.println("Server is run");
        Socket clientSocket = null;


        try {
            while (true) {
                clientSocket = serverSocket.accept();
                Handler handler = new Handler(clientSocket);
                handler.start();
            }
        } catch (IOException e) {
            clientSocket.close();
            System.out.println("Server stopped");
            serverSocket.close();
        }



    }

    public static void sendBroadcastMessage(Message message) {
        for (String name : connectionMap.keySet()) {
            try {
                connectionMap.get(name).send(message);
            } catch (IOException e) {
                System.out.println("Can not send the message");
            }
        }
    }
}

