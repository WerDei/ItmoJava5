package net.werdei.talechars.server.collections;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Character implements Comparable<Character>, Serializable
{
    private String name;
    private String[] nicknames;
    private String description;
    private int power;
    private Location location;
    private LocalDate birthDate;

    public Character(String name)
    {
        this.name = name;
        initAfterJson();
    }

    public Character(String name, String description, String... nicknames)
    {
        this.name = name;
        this.nicknames = nicknames;
        this.description = description;
        initAfterJson();
    }

    public void initAfterJson()
    {
        if (name == null)
            throw new JsonCharacterNameNotGiven("Variable \"name\" is required");

        if (description == null)
            description = "A character";

        if (location == null)
            location = new Location(0, 100, 0);

        if (birthDate == null)
            birthDate = LocalDate.now();
    }


    @Override
    public int compareTo(Character o)
    {
        return name.compareTo(o.name);
    }

    @Override
    public String toString()
    {
        // creating a pretty formatted list of nicknames if there are any
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

        // Formatting date of birth
        String birthDateString = birthDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        return name + nicknamesString + " - " + description + "; Birth date - " + birthDateString + "; " + location;
    }

    public class Location
    {
        public float x;
        public float y;
        public float z;

        public Location(float x, float y, float z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String toString() {
            return String.format("[%.1f; %.1f; %.1f]", x, y, z);
        }
    }
}
