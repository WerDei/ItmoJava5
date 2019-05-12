package net.werdei.talechars;

import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        CollectionHandler charCollection = new CollectionHandler();

        if (args.length == 1)
            charCollection.loadFromFile(args[0]);

        Commands.collection = charCollection;

        //addDebugElementsToCollection(charCollection);

        Scanner reader = new Scanner(System.in);
        while (Commands.collection != null)
        {
            String[] line = reader.nextLine().split(" ", 2);
            if (line.length == 2)
                Commands.execute(line[0], line[1]);
            else if (line.length == 1)
                Commands.execute(line[0]);
        }
        reader.close();
    }

    private static void addDebugElementsToCollection(CollectionHandler c)
    {
        c.addCharacter(new Character("WerDei", "The creator"));
        c.addCharacter(new Character("Matt Mercer", "The best DM to ever DM", "God", "How do you want to do this?"));
        c.addCharacter("{name = \"The user\"}");
    }
}
