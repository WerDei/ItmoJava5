package net.werdei.talechars.server;

import net.werdei.talechars.NetworkInfo;
import net.werdei.talechars.server.collections.CollectionHandler;

import java.net.ServerSocket;
import java.util.ArrayList;

public class Main
{
    private static ServerSocket serverSocket;
    private static ArrayList<UserThread> connectedUsers;

    private static CollectionHandler collectionHandler;

    public static void main(String[] args)
    {

        // Emergency saving. Commented because this pile of steaming shit doesn't work.
        Runtime.getRuntime().addShutdownHook(new Thread(
                () ->  collectionHandler.saveToFile("backup.json", null)));


        int port = NetworkInfo.defaultPort;
        try
        {
            port = Integer.parseInt(args[1]);
        }
        catch (Exception e){
            System.out.println("Using the default port");
        }

        connectedUsers = new ArrayList<>();

        collectionHandler = new CollectionHandler();

        try
        {
            serverSocket = new ServerSocket(port);

            System.out.println("Successfully started the server");
            System.out.println("Waiting for connections on port " + port);

            while (true)
            {
                UserThread userThread = new UserThread(serverSocket.accept(), collectionHandler);
                connectedUsers.add(userThread);
                userThread.start();
            }
        }
        catch (Exception e)
        {
            System.out.println("[ERROR] Error binding port: " + e.getMessage());
        }
    }
}
