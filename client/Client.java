package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.Connection;
import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.Message;
import com.javarush.task.task30.task3008.MessageType;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private Connection connection;
    private volatile boolean clientConnected = false;

    public class SocketThread extends Thread {
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeMessage(userName + " присоеденился к чату.");
        }

        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage(userName + " покинул чат.");
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }

        public void run() {
            try {
                Socket socket = new Socket(getServerAddress(), getServerPort());
                connection = new Connection(socket);
                clientHandshake();
                clientMainLoop();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                notifyConnectionStatusChanged(false);
            }


        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message serverRequest = connection.receive();
                String name;
                MessageType type;
                if (serverRequest.getType() == MessageType.NAME_REQUEST) {
                    name = getUserName();
                    type = MessageType.USER_NAME;
                    connection.send(new Message(type, name));
                } else if (serverRequest.getType() == MessageType.NAME_ACCEPTED) {
                    notifyConnectionStatusChanged(true);
                    return;
                } else {
                    throw new IOException("Unexpected MessageType");
                }
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            while (true) {
                Message serverRequest = connection.receive();
                if (serverRequest.getType() == MessageType.TEXT) {
                    processIncomingMessage(serverRequest.getData());
                } else if (serverRequest.getType() == MessageType.USER_ADDED) {
                    informAboutAddingNewUser(serverRequest.getData());
                } else if (serverRequest.getType() == MessageType.USER_REMOVED) {
                    informAboutDeletingNewUser(serverRequest.getData());
                } else {
                    throw new IOException("Unexpected MessageType");
                }
            }
        }
    }

    protected String getServerAddress() throws IOException {

        return ConsoleHelper.readString();
    }

    protected int getServerPort() throws IOException {
        return ConsoleHelper.readInt();
    }

    protected String getUserName() throws IOException {
        return ConsoleHelper.readString();
    }

    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    protected void sendTextMessage(String text) {
        try {
            Message message = new Message(MessageType.TEXT, text);
            connection.send(message);
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Error");
            clientConnected = false;
        }
    }

    public void run() {
        SocketThread thread = getSocketThread();
        getSocketThread().setDaemon(true);
        thread.start();
        try {
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException e) {
            ConsoleHelper.writeMessage("There was an error, program will be stopped");
            System.exit(0);
        }
        if (clientConnected) {
            ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду 'exit'.");
        } else {
            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
        }
        while (clientConnected) {
            String mess = ConsoleHelper.readString();
            if (mess.equals("exit")) return;
            if (shouldSendTextFromConsole() == true) {
                sendTextMessage(mess);
            }
        }

    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
