package net.werdei.talechars.client;

import net.werdei.talechars.NetworkInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public final class Main
{
    // Networking
    private static Socket socket;
    private static boolean connectionEstablished = false;

    // Data
    private static DataOutputStream out;
    private static DataInputStream in;
    private static MessageReadThread messageReadThread;

    // User Input
    private static Scanner reader;


    static public void main(String[] args)
    {
        reader = new Scanner(System.in);
        socket = new Socket();
        InetAddress address = null;
        int port = NetworkInfo.defaultPort;


        // Trying to use address and port from input, if failing - using the defaults
        try
        {
            address = InetAddress.getByName(NetworkInfo.defaultAddress);
            address = InetAddress.getByName(args[0]);
        }
        catch (Exception e){
            System.out.println("Using the default address");
        }

        try
        {
            port = Integer.parseInt(args[1]);
        }
        catch (Exception e){
            System.out.println("Using the default port");
        }


        // Main loop
        while (true)
        {
            if (!connectionEstablished)
                if (!connect(address, port, 10))
                    break;

            readInputAndSendToServer();

            if (/* user wants to exit the program */ false)
            {
                break;
            }
        }

        reader.close();
    }

    private static boolean connect(InetAddress address, int port, int attempts)
    {
        System.out.println("Connecting to " + address + " on port " + port);

        while (!connectionEstablished)
        {
            --attempts;
            if (attempts <= 0)
            {
                System.out.println();
                System.out.println("Server is currently unavailable. " +
                        "Check the connection data or try again later.");
                return false;
            }

            attemptConnection(address, port);

            System.out.print(" .");
        }

        System.out.println(" connection established.");
        return true;
    }

    private static void attemptConnection(InetAddress address, int port)
    {
        try
        {
            socket.close();
            socket = new Socket(address, port);

            //If no exceptions were thrown, we have successfully connected
            connectionEstablished = true;

            // Initialising input and output streams
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            // Start waiting for the server messages
            messageReadThread = new MessageReadThread();
            messageReadThread.start();
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }

    private static void readInputAndSendToServer()
    {
        try
        {
            String line = reader.nextLine();
            out.writeUTF(line);
            System.out.println("Request sent");
        }
        catch (SocketException e)
        {
            System.out.println("Lost connection to server");
            connectionEstablished = false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static class MessageReadThread extends Thread
    {
        @Override
        public void run() {
            while (connectionEstablished)
            {
                try
                {
                    System.out.println(in.readUTF());
                }
                catch (Exception e)
                {
                    connectionEstablished = false;
                }
            }
        }
    }
}
