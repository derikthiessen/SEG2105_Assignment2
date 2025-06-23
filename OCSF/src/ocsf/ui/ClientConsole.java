package ocsf.ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import ocsf.client.ChatClient;
import ocsf.common.ChatIF;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String loginId, String host, int port) 
  {
    try 
    {
      client= new ChatClient(loginId, host, port, this);
      
      
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
/**
 * This method waits for input from the console.  Once it is 
 * received, it sends it to the client's message handler.
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
        client.handleMessageFromClientUI(message);
      }
    }
  } 
  catch (Exception ex) 
  {
    System.out.println
      ("Unexpected error while reading from console!");
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
          client.closeConnection();
        } catch (Exception e) {}
        System.exit(0);
        break;

      case "logoff":
        try {
          client.closeConnection();
          System.out.println("Logged off from server.");
        } catch (Exception e) {
          System.out.println("Error logging off: " + e.getMessage());
        }
        break;

      case "sethost":
        if (client.isConnected()) {
          System.out.println("You must log off before changing the host.");
        } else if (tokens.length > 1) {
          client.setHost(tokens[1]);
          System.out.println("Host set to: " + tokens[1]);
        } else {
          System.out.println("Usage: #sethost <host>");
        }
        break;

      case "setport":
        if (client.isConnected()) {
          System.out.println("You must log off before changing the port.");
        } else if (tokens.length > 1) {
          try {
            int port = Integer.parseInt(tokens[1]);
            client.setPort(port);
            System.out.println("Port set to: " + port);
          } catch (NumberFormatException e) {
            System.out.println("Invalid port number. Usage: #setport <port>");
          }
        } else {
          System.out.println("Usage: #setport <port>");
        }
        break;

      case "login":
        if (client.isConnected()) {
          System.out.println("You are already logged in.");
        } else {
          try {
            client.openConnection();
            System.out.println("Logged in to server at " + client.getHost() + ":" + client.getPort());
          } catch (Exception e) {
            System.out.println("Error logging in: " + e.getMessage());
          }
        }
        break;

      case "gethost":
        System.out.println("Current host: " + client.getHost());
        break;

      case "getport":
        System.out.println("Current port: " + client.getPort());
        break;

      default:
        System.out.println("Unknown command: #" + cmd);
        System.out.println("Available commands: #quit, #logoff, #sethost <host>, #setport <port>, #login, #gethost, #getport");
        break;
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
public static void main(String[] args) 
  {
    if (args.length < 1) {
      System.out.println("ERROR - No login ID specified. Connection aborted.");
      System.exit(1);
    }
    String loginId = args[0];
    String host = "localhost";
    int port = DEFAULT_PORT;

    if (args.length > 1) {
      host = args[1];
    }
    if (args.length > 2) {
      try {
        port = Integer.parseInt(args[2]);
      } catch (NumberFormatException e) {
        System.out.println("Invalid port number. Using default port " + DEFAULT_PORT);
        port = DEFAULT_PORT;
      }
    }

    ClientConsole chat = new ClientConsole(loginId, host, port);
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class
