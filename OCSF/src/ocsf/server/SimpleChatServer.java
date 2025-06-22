package ocsf.server;

public class SimpleChatServer extends AbstractServer {

    /**
     * Constructs the server with the specified port.
     *
     * @param port the port number.
     */
    public SimpleChatServer(int port) {
        super(port);
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println("Received message from client: " + msg);
        // Echo the message back to the client
        try {
            client.sendToClient("Echo: " + msg);
        } catch (Exception e) {
            System.out.println("Error sending message to client: " + e.getMessage());
        }
    }    

    @Override
    protected void clientConnected(ConnectionToClient client) {
        System.out.println("Client connected: " + client.getInetAddress());
    }

    @Override
    synchronized protected void clientDisconnected(ConnectionToClient client) {
        System.out.println("Client disconnected: " + client.getInetAddress());
    }
}
