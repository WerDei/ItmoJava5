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
        connectedUsers = new ArrayList<>();

        try
        {
            serverSocket = new ServerSocket(NetworkInfo.defaultPort);

            System.out.println("Successfully started the server");
            System.out.println("Waiting for connections on port " + NetworkInfo.defaultPort);

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
