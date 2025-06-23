package ocsf.ui;

import java.util.Scanner;
import ocsf.server.EchoServer;
import ocsf.common.ChatIF;

/**
 * This class constructs the UI for a chat server. It implements the
 * chat interface in order to activate the display() method.
 */
public class ServerConsole implements ChatIF
{
  final public static int DEFAULT_PORT = 5555;

  private EchoServer server;
  private Scanner fromConsole;

  /**
   * Constructs an instance of the ServerConsole UI.
   * @param port The port to listen on.
   */
  public ServerConsole(int port)
  {
    server = new EchoServer(port);
    fromConsole = new Scanner(System.in);
  }

  /**
   * This method waits for input from the console. Once it is
   * received, it sends it to the server's message handler.
   */
  public void accept()
  {
    try
    {
      String message;
      while (true)
      {
        message = fromConsole.nextLine();
        if (message.startsWith("#")) {
          handleCommand(message.substring(1).trim());
        } else {
          String serverMsg = "SERVER MSG> " + message;
          display(serverMsg);
          server.sendToAllClients(serverMsg);
        }
      }
    }
    catch (Exception ex)
    {
      System.out.println("Unexpected error while reading from console!");
    }
  }

  /**
   * Handles commands entered by the user that start with '#'.
   * @param command The command string without the leading '#'.
   */
  private void handleCommand(String command) {
    String[] tokens = command.split("\\s+");
    String cmd = tokens[0].toLowerCase();

    switch (cmd) {
      case "quit":
        try {
          server.close();
        } catch (Exception e) {}
        System.exit(0);
        break;

      case "stop":
        try {
          server.stopListening();
        } catch (Exception e) {
          System.out.println("Error stopping server: " + e.getMessage());
        }
        break;

      case "close":
        try {
          server.close();
          System.out.println("Server closed and all clients disconnected.");
        } catch (Exception e) {
          System.out.println("Error closing server: " + e.getMessage());
        }
        break;

      case "setport":
        if (server.isListening() || server.getNumberOfClients() > 0) {
          System.out.println("You must close the server before changing the port.");
        } else if (tokens.length > 1) {
          try {
            int port = Integer.parseInt(tokens[1]);
            server.setPort(port);
            System.out.println("Port set to: " + port);
          } catch (NumberFormatException e) {
            System.out.println("Invalid port number. Usage: #setport <port>");
          }
        } else {
          System.out.println("Usage: #setport <port>");
        }
        break;

      case "start":
        if (server.isListening()) {
          System.out.println("Server is already listening for clients.");
        } else {
          try {
            server.listen();
            System.out.println("Server started listening for new clients.");
          } catch (Exception e) {
            System.out.println("Error starting server: " + e.getMessage());
          }
        }
        break;

      case "getport":
        System.out.println("Current port: " + server.getPort());
        break;

      default:
        System.out.println("Unknown command: #" + cmd);
        System.out.println("Available commands: #quit, #stop, #close, #setport <port>, #start, #getport");
        break;
    }
  }

  /**
   * This method overrides the method in the ChatIF interface. It
   * displays a message onto the screen.
   * @param message The string to be displayed.
   */
  public void display(String message)
  {
    System.out.println("> " + message);
  }

  /**
   * Main method to start the server console.
   * @param args[0] The port to listen on.
   */
  public static void main(String[] args)
  {
    int port = DEFAULT_PORT;
    try {
      port = Integer.parseInt(args[0]);
    } catch (Exception e) {
      port = DEFAULT_PORT;
    }
    ServerConsole sc = new ServerConsole(port);
    try {
      sc.server.listen();
    } catch (Exception e) {
      System.out.println("ERROR - Could not listen for clients!");
    }
    sc.accept();
  }
}
