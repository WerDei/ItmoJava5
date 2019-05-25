package net.werdei.talechars.server;

import net.werdei.talechars.server.userside.Character;
import net.werdei.talechars.server.userside.CollectionHandler;
import net.werdei.talechars.server.userside.Commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class UserThread extends Thread
{
    private Socket socket;
    private String alias;

    private DataInputStream in;
    private DataOutputStream out;

    private CollectionHandler collection;


    public UserThread(Socket s)
    {
        socket = s;
        alias = socket.getInetAddress().toString();

        System.out.println("Established a connection with " + alias);
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

    private void createAndInitialiseCollection()
    {
        // Создание и загрузка коллекции
        collection = new CollectionHandler(this);
        collection.loadFromFile("backup.json");
    }


    @Override
    public void run()
    {
        if (!setupDataStreams())
            return;

        createAndInitialiseCollection();

        while (true)
        {
            try
            {
                String input = in.readUTF();
                System.out.println(alias + " > " + input);

                Commands.execute(input, this);
            }
            catch (SocketException e)
            {
                System.out.println("Connection with " + alias + " has been lost");
                return;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return;
            }
        }
    }

    public void sendln(String message)
    {
        try {
            out.writeUTF(message);
        }
        catch (Exception e)
        {
            System.out.println("[ERROR] Error sending a message to " + alias);
            e.printStackTrace();
        }
    }

    public CollectionHandler getCollection()
    {
        return collection;
    }
    private static void addDebugElementsToCollection(CollectionHandler c)
    {
        c.addCharacter(new Character("WerDei", "The creator"));
        c.addCharacter(new Character("Matt Mercer", "The best DM to ever DM", "God", "How do you want to do this?"));
        c.addCharacter("{name = \"The user\"}");
    }
}
