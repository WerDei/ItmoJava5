package net.werdei.talechars.server.userside;

public class Character implements Comparable<Character>
{
    private String name;
    private String[] nicknames;
    private String description;


    public Character(String name, String description, String... nicknames)
    {
        this.name = name;
        this.nicknames = nicknames;
        this.description = description;
    }

    public void initAfterJson()
    {
        if (name == null)
            throw new JsonCharacterNameNotGiven("Variable \"name\" is required");

        if (description == null)
            description = "A character";
    }


    @Override
    public int compareTo(Character o)
    {
        return name.compareTo(o.name);
    }

    @Override
    public String toString()
    {
        String nicknamesString = "";
        if (nicknames != null && nicknames.length > 0)
        {
            nicknamesString += " (";
            for (int i = 0; i < nicknames.length; i++)
            {
                nicknamesString += nicknames[i];
                nicknamesString += i == nicknames.length - 1 ? ")" : "; ";
            }
        }

        return name + nicknamesString + " - " + description;
    }
}
