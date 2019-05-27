package net.werdei.talechars.server.collections;

import com.google.gson.JsonSyntaxException;
import net.werdei.talechars.CommandParser;
import net.werdei.talechars.server.UserThread;

import java.util.List;

public enum Commands {
    HELP("help")
            {
                @Override
                protected boolean executeCommand(List<String> args, UserThread userThread)
                {
                    if (args.size() == 0)
                    {
                        userThread.sendln("There are " + Commands.values().length + " available commands:");

                        for (Commands c : Commands.values())
                            userThread.sendln(c.getUsage());
                    }
                    else if (args.size() == 1)
                    {
                        Commands c = Commands.findCommand(args.get(0));
                        if (c != null)
                            userThread.sendln(c.getUsage());
                        else
                            userThread.sendln("There is no such command as \"" + args.get(0) + "\"");
                    }
                    else
                        return false;

                    return true;
                }

                @Override
                protected String getUsage() {
                    return "help\n"
                        + "help <command>";
                }
            },
    INFO("info")
            {
                @Override
                protected boolean executeCommand(List<String> args, UserThread userThread) {
                    if (args.size() == 0) {
                        userThread.getCollection().printInfo();
                        return true;
                    }
                    else return false;
                }

                @Override
                protected String getUsage() {
                    return "info";
                }
            },
    SHOW("show")
            {
                @Override
                protected boolean executeCommand(List<String> args, UserThread userThread) {
                    if (args.size() == 0) {
                        userThread.getCollection().printElements();
                        return true;
                    }
                    else if ((args.size() == 1) && (args.contains("-json")))
                    {
                        userThread.sendln(userThread.getCollection().getJsonFromCollection());
                        return true;
                    }
                    else return false;
                }

                @Override
                protected String getUsage() {
                    return "show\n"
                            + "show -json";
                }
            },
    ADD("add")
            {
                @Override
                protected boolean executeCommand(List<String> args, UserThread userThread) {
                    if (args.size() == 1)
                    {
                        addElement(args.get(0), userThread);
                        return true;
                    }
                    else if (args.size() == 2 && args.contains("-ifmax"))
                    {
                        if (userThread.getCollection().compareToMax(args.get(1)) > 0)
                            addElement(args.get(1), userThread);
                        else
                            userThread.sendln("Element is not bigger than "
                                    + "the existing ones and was not added");
                        return true;
                    }
                    return false;
                }

                private void addElement(String json, UserThread userThread)
                {
                    try {
                        userThread.getCollection().addCharacter(json);
                        userThread.sendln("Element successfully added");
                    }
                    catch (JsonSyntaxException | JsonCharacterNameNotGiven e)
                    {
                        userThread.sendln("Incorrect JSON syntax: " + e.getMessage());
                    }
                }

                @Override
                protected String getUsage() {
                    return "add <element>\n"
                            + "add -ifmax <element>";
                }
            },
    REMOVE("remove")
            {
                @Override
                protected boolean executeCommand(List<String> args, UserThread userThread) {
                    if (args.size() == 1)
                    {
                        removeElement(args.get(0),userThread);
                        return true;
                    }
                    return false;
                }

                private void removeElement(String json, UserThread userThread)
                {
                    try {
                        userThread.getCollection().removeCharacter(json);
                        userThread.sendln("Element removed successfully");
                    }
                    catch (JsonSyntaxException | JsonCharacterNameNotGiven e) {
                        userThread.sendln("Incorrect JSON syntax: " + e.getMessage());
                    }
                }

                @Override
                protected String getUsage() {
                    return "remove <element>";
                }
            },
    LOAD("load")
            {
                @Override
                protected boolean executeCommand(List<String> args, UserThread userThread) {
                    if (args.size() == 0)
                    {
                        userThread.getCollection().loadFromFile("backup.json");
                        return true;
                    }
                    return false;
                }

                @Override
                protected String getUsage() {
                    return "load";
                }
            },
    SAVE("save")
            {
                @Override
                protected boolean executeCommand(List<String> args, UserThread userThread) {
                    if (args.size() == 0)
                    {
                        userThread.getCollection().saveToFile();
                        return true;
                    }
                    return false;
                }

                @Override
                protected String getUsage() {
                    return "save";
                }
            },
    IMPORT("import")
            {
                @Override
                protected boolean executeCommand(List<String> args, UserThread userThread)
                {
                    if (args.size() == 2 && args.get(0).equals("-json"))
                    {
                        userThread.getCollection().loadFromJson(args.get(1));
                        return true;
                    }
                    return false;
                }

                @Override
                protected String getUsage() {
                    return "import <file path>\n" +
                            "import -json <collection>";
                }
            },
    EXIT("exit")
            {
                @Override
                protected boolean executeCommand(List<String> args, UserThread userThread)
                {
                    userThread.setConnectedStatus(false);
                    return true;
                }

                @Override
                protected String getUsage() {
                    return "exit";
                }
            }
            ;

    public static void execute(String input, UserThread userThread)
    {
        List<String> args = CommandParser.parse(input);
        String command = args.get(0);
        args.remove(command);

        Commands c = findCommand(command);
        if (c != null)
        {
            boolean usage = c.executeCommand(args, userThread);
            if (!usage)
            {
                userThread.sendln("Correct usage:");
                userThread.sendln(c.getUsage());
            }
        }
        else
            userThread.sendln("Unknown command. Use \"help\" to see the list of commands");
    }


    protected String commandName;

    Commands(String command)
    {
        commandName = command;
    }

    private static Commands findCommand(String name)
    {
        for (Commands c : Commands.values()) {
            if (c.commandName.equals(name)) {
                return c;
            }
        }
        return null;
    }

    //Should return False is the usage of the command is incorrect
    protected abstract boolean executeCommand(List<String> args, UserThread userThread);

    protected abstract String getUsage();
}
