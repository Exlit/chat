package com.javarush.task.task30.task3008.client;

public class ClientGuiController extends Client {

    public class GuiSocketThread extends SocketThread {
        protected void processIncomingMessage(String message) {
            model.setNewMessage(message);
            view.refreshMessages();
        }

        protected void informAboutAddingNewUser(String userName) {
            model.addUser(userName);
            view.refreshUsers();
        }

        protected void informAboutDeletingNewUser(String userName) {
            model.deleteUser(userName);
            view.refreshUsers();
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            view.notifyConnectionStatusChanged(clientConnected);
        }
    }

    private ClientGuiModel model = new ClientGuiModel();
    private ClientGuiView view = new ClientGuiView(this);

    protected SocketThread getSocketThread() {
        return new GuiSocketThread();
    }

    public void run() {
        getSocketThread().run();
    }

    @Override
    protected String getServerAddress() {
        return view.getServerAddress();
    }

    @Override
    protected int getServerPort() {
        return view.getServerPort();
    }

    @Override
    protected String getUserName() {
        return view.getUserName();
    }

    public ClientGuiModel getModel() {
        return model;
    }

    public static void main(String[] args) {
        ClientGuiController controller = new ClientGuiController();
        controller.run();
    }
}
