package ocsf.client;

import java.io.IOException;

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

    public static void main(String[] args) {
        String host = "defaulthost";
        int port = 5555;

        if (args.length > 0) {
            host = args[0];
        }

        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number. Using default port 5555.");
            }
        }

        SimpleChatClient client = new SimpleChatClient(host, port);
        try {
            client.openConnection();
        } catch (IOException e) {
            System.out.println("Could not connect to server at " + host + ":" + port);
            System.exit(1);
        }
    }
}
