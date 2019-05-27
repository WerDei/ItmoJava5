package net.werdei.talechars.server;

import net.werdei.talechars.server.collections.CollectionHandler;
import net.werdei.talechars.server.collections.Commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class UserThread extends Thread
{
    private Socket socket;
    private String alias;
    private boolean connected = true;

    private DataInputStream in;
    private DataOutputStream out;

    private CollectionHandler collection;


    public UserThread(Socket s, CollectionHandler collectionHandler)
    {
        socket = s;
        alias = socket.getInetAddress().toString();

        System.out.println("Established a connection with " + alias);

        connected = setupDataStreams();

        collection = collectionHandler;
    }

    private boolean setupDataStreams()
    {
        try
        {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            return true;
        }
        catch (IOException e)
        {
            System.out.println("[ERROR] Could not initialise data streams");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void run()
    {
        try
        {
            while (connected)
            {
                String input = in.readUTF();
                System.out.println(alias + " > " + input);

                Commands.execute(input, this);
            }
        }
        catch (SocketException e)
        {
            connected = false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            connected = false;
        }
        finally
        {
            System.out.println("Connection with " + alias + " has been lost");
        }

        System.out.println("Saving the collection of user " + alias);
        collection.saveToFile(this);
    }

    public void sendln(String message)
    {
        if (connected) {
            try {
                out.writeUTF(message);
            } catch (Exception e) {
                System.out.println("[ERROR] Error sending a message to " + alias);
                e.printStackTrace();
                connected = false;
            }
        }
    }

    public CollectionHandler getCollection()
    {
        return collection;
    }

    public String getAlias()
    {
        return alias;
    }

    public void setConnectedStatus(boolean status)
    {
        connected = status;
    }
}
