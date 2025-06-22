package ocsf.client;

public class SimpleChatClient extends AbstractClient {
    
    /**
     * Constructs the client with the specified host and port.
     *
     * @param host the server's host name.
     * @param port the port number.
     */
    public SimpleChatClient(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        System.out.println("Print message from server as a placeholder");
    }

    @Override
    protected void connectionClosed() {
		System.out.println("Connection closed: server has shut down.");
		System.exit(0);
	}

    @Override
    protected void connectionException(Exception exception) {
        System.out.println("Connection error: server has shut down or an exception occured when connecting.");
		System.out.println(exception.getMessage());
		System.exit(0);
    }
}
