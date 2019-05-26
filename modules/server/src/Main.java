package net.werdei.talechars.server;

import net.werdei.talechars.NetworkInfo;

import java.net.ServerSocket;
import java.util.ArrayList;

public class Main
{
    private static ServerSocket serverSocket;
    private static ArrayList<UserThread> connectedUsers;

    public static void main(String[] args)
    {
        // Emergency saving
        Runtime.getRuntime().addShutdownHook(new Thread(
                () -> connectedUsers.forEach(x -> x.getCollection().saveToFile())));

        int port = NetworkInfo.defaultPort;
        try
        {
            port = Integer.parseInt(args[1]);
        }
        catch (Exception e){
            System.out.println("Using the default port");
        }

        connectedUsers = new ArrayList<>();

        try
        {
            serverSocket = new ServerSocket(port);

            System.out.println("Successfully started the server");
            System.out.println("Waiting for connections on port " + port);

            while (true)
            {
                UserThread userThread = new UserThread(serverSocket.accept());
                connectedUsers.add(userThread);
                userThread.start();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
