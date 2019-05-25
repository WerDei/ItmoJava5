package net.werdei.talechars.server.collections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.werdei.talechars.server.UserThread;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Stream;

public class CollectionHandler
{
    private UserThread userThread;

    private File file;
    private TreeSet<Character> characters;
    private LocalDate creationDate;
    private LocalTime creationTime;
    private LocalDate modificationDate;
    private LocalTime modificationTime;


    public CollectionHandler(UserThread thread)
    {
        userThread = thread;

        characters = new TreeSet<>();
        creationTime = modificationTime = LocalTime.now();
        creationDate = modificationDate = LocalDate.now();
    }


    //Getters

    public int getLength()
    {
        return characters.size();
    }

    public String getJsonFromCollection()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(characters);
    }


    // Modification

    public void addCharacter(String json)
    {
        addCharacter(getCharacterFromJson(json));
    }

    public void addCharacter(Character c)
    {
        if(characters.add(c))
            collectionUpdated();
        else
            userThread.sendln("Character with that name already exists");
    }

    public void removeCharacter(String json)
    {
        removeCharacter(getCharacterFromJson(json));
    }

    public void removeCharacter(Character c)
    {
        if(characters.remove(c))
            collectionUpdated();
        else
            userThread.sendln("No such character was found");
    }


    // Saving and loading

    public void loadFromFile(String filePath)
    {
        try
        {
            file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            String jsonString = "";
            while ((line = bufferedReader.readLine()) != null)
                jsonString += line;

            loadFromJson(jsonString);

            creationDate = LocalDate.now();
            creationTime = LocalTime.now();

            userThread.sendln("Collection successfully loaded");
        }
        catch (Exception e)
        {
            userThread.sendln("Loading failed: " + e.getMessage());
        }
    }

    public void loadFromJson(String json)
    {
        Gson gson = new Gson();
        Character[] characterDummyArray = gson.fromJson(json, Character[].class);

        characters.addAll(Arrays.asList(characterDummyArray));
    }

    public void saveToFile(String filePath)
    {
        file = new File(filePath);
        saveToFile();
    }

    public void saveToFile()
    {
        if (file == null)
            throw new RuntimeException("File is not initialised");

        String json = getJsonFromCollection();

        String[] jsonLines = json.split("\n");

        try {
            file.createNewFile();
            PrintWriter writer = new PrintWriter(file);

            for (String line : jsonLines) {
                writer.println(line);
            }

            writer.close();

            userThread.sendln("Collection successfully saved");
        }
        catch (Exception e)
        {
            userThread.sendln("Saving failed: " + e.getMessage());
            System.out.println("Saving failed: " + e.getMessage());
        }
    }


    // Returning information

    public int compareToMax(String json)
    {
        return getCharacterFromJson(json).compareTo(characters.last());
    }


    // Printing information

    public void printElements()
    {
        userThread.sendln("Current collection elements:");

        Object[] array = characters.toArray();
        Stream.iterate(1, x -> x + 1)
                .limit(array.length)
                .map(x -> x + ": " + array[x - 1].toString())
                .forEach(userThread::sendln);
    }

    public void printInfo()
    {
        userThread.sendln("Element count: " + getLength());
        userThread.sendln("First population: " + creationDate + " " + creationTime);
        userThread.sendln("Last modification: " + modificationDate + " " + modificationTime);
    }


    // Utility

    private Character getCharacterFromJson(String json)
    {
        Gson gson = new Gson();
        Character c = gson.fromJson(json, Character.class);
        c.initAfterJson();
        return c;
    }

    private void collectionUpdated()
    {
        modificationTime = LocalTime.now();
        modificationDate = LocalDate.now();
    }
}
