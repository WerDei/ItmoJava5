package net.werdei.talechars;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.TreeSet;

public class CollectionHandler
{
    private File file;
    private TreeSet<Character> characters;
    private LocalDate creationDate;
    private LocalTime creationTime;
    private LocalDate modificationDate;
    private LocalTime modificationTime;


    public CollectionHandler()
    {
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
            collectionUpdaded();
        else
            System.out.println("Character with that name already exists");
    }

    public void removeCharacter(String json)
    {
        removeCharacter(getCharacterFromJson(json));
    }

    public void removeCharacter(Character c)
    {
        if(characters.remove(c))
            collectionUpdaded();
        else
            System.out.println("No such character was found");
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

            Gson gson = new Gson();
            Character[] characterDummyArray = gson.fromJson(jsonString, Character[].class);

            characters.addAll(Arrays.asList(characterDummyArray));

            creationDate = LocalDate.now();
            creationTime = LocalTime.now();

            System.out.println("Successfully loaded collection from " + file.getAbsolutePath());
        }
        catch (Exception e)
        {
            System.out.println("Loading failed: " + e.getMessage());
        }
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

            System.out.println("Successfully saved collection to " + file.getAbsolutePath());
        }
        catch (Exception e)
        {
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
        System.out.println("Current collection elements:");

        int i = 0;
        for (Character character : characters)
        {
            System.out.println(++i + ": " + character.toString());
        }
    }

    public void printInfo()
    {
        System.out.println("Element count: " + getLength());
        System.out.println("First population: " + creationDate + " " + creationTime);
        System.out.println("Last modification: " + modificationDate + " " + modificationTime);
    }


    // Utility

    private Character getCharacterFromJson(String json)
    {
        Gson gson = new Gson();
        Character c = gson.fromJson(json, Character.class);
        c.initAfterJson();
        return c;
    }

    private void collectionUpdaded()
    {
        modificationTime = LocalTime.now();
        modificationDate = LocalDate.now();
    }
}
