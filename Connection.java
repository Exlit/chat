package com.javarush.task.task30.task3008;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection implements Closeable {
    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public void send(Message message) throws IOException
    {

        synchronized (out)
        {
            out.writeObject(message);
        }
    }

   public Message receive() throws IOException, ClassNotFoundException
    {
        synchronized (in)
        {
            return (Message) in.readObject();
        }
    }

    public SocketAddress getRemoteSocketAddress()
    {
        return socket.getRemoteSocketAddress();
    }

    @Override
    public void close() throws IOException {
      if (socket != null) socket.close();
      if (out != null) out.close();
      if (in != null) in.close();
    }
}
