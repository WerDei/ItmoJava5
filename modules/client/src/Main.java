package net.werdei.talechars.client;

import net.werdei.talechars.NetworkInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
        String address = NetworkInfo.defaultAddress;
        int port = NetworkInfo.defaultPort;

        try
        {
            address = args[0];
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


        reader = new Scanner(System.in);
        socket = new Socket();

        while (true)
        {
            while (!connectionEstablished)
            {
                attemptConnection(address, port);
            }

            readInputAndSendToServer();

            if (/* user wants to exit the program */ false)
            {
                break;
            }
        }

        reader.close();
    }

    private static void attemptConnection(String address, int port)
    {
        System.out.println("Attempting a connection to " + address + " on port " + port);

        try
        {
            socket.close();
            socket = new Socket(address, port);

            //If no exceptions were thrown, we have successfully connected
            System.out.println("Connection established");
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
