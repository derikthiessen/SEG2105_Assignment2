package ocsf.client;

import java.io.IOException;
import java.util.Scanner;

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

    private boolean handleCommand(String command, SimpleChatClient client) {
        String[] commands = command.split(" ");

        switch (commands[0]) {
            case "quit":
                try {
                    client.closeConnection();
                } catch (IOException e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                }

                return false;

            case "logoff":
                try {
                    client.closeConnection();
                } catch (IOException e) {
                    System.out.println("Error logging off: " + e.getMessage());
                }

                return true;

            case "sethost":
                if (client.isConnected()) {
                    System.out.println("You must log off before changing the host.");
                } else {
                    if (commands.length > 1) {
                        client.setHost(commands[1]);
                        System.out.println("Host set to: " + commands[1]);
                    } else {
                        System.out.println("Usage: #sethost <hostname>");
                    }
                }

                return true;

            case "setport":
                
                if (client.isConnected()) {
                    System.out.println("You must log off before changing the port.");
                } else {
                    if (commands.length > 1) {
                        try {
                            int port = Integer.parseInt(commands[1]);
                            client.setPort(port);
                            System.out.println("Port set to: " + port);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid port number. Usage: #setport <port>");
                        }
                    } else {
                        System.out.println("Usage: #setport <port>");
                    }
                }

                return true;

            case "login":
                if (client.isConnected()) {
                    System.out.println("You are already logged in.");
                } else {
                    try {
                        client.openConnection();
                        System.out.println("Logged in to server at " + client.getHost() + ":" + client.getPort());
                    } catch (IOException e) {
                        System.out.println("Error logging in: " + e.getMessage());
                    }
                }

                return true;

            case "gethost":
                System.out.println("Current host: " + client.getHost());
                return true;
            
            case "getport":
                System.out.println("Current port: " + client.getPort());
                return true;
            
            default:
                System.out.println("Unknown command: " + commands[0]);
                System.out.println("Available commands: quit, logoff, sethost, setport, login, gethost, getport");
                return true;
        }
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

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            String input = scanner.nextLine();
            if (input.startsWith("#")) {
                running = client.handleCommand(input.substring(1).toLowerCase().trim(), client);
            }
            else {
                try {
                    client.sendToServer(input);
                } catch (IOException e) {
                    System.out.println("Error sending message to server: " + e.getMessage());
                }
            }
        }

        scanner.close();
    }
}
