package net.werdei.talechars;

import com.google.gson.JsonSyntaxException;

public enum Commands {
    HELP("help")
            {
                @Override
                protected boolean executeCommand(String[] args)
                {
                    if (args.length == 0)
                    {
                        System.out.println("There are " + Commands.values().length + " available commands:");

                        for (Commands c : Commands.values())
                            c.printUsage();
                    }
                    else if (args.length == 1)
                    {
                        Commands c = Commands.findCommand(args[0]);
                        if (c != null)
                            c.printUsage();
                        else
                            System.out.println("There is no such command as \"" + args[0] + "\"");
                    }
                    else return false;

                    return true;
                }

                @Override
                protected void printUsage() {
                    System.out.println("help\n"
                        + "help <command>");
                }
            },
    INFO("info")
            {
                @Override
                protected boolean executeCommand(String[] args) {
                    if (args.length == 0) {
                        collection.printInfo();
                        return true;
                    }
                    else return false;
                }

                @Override
                protected void printUsage() {
                    System.out.println("info");
                }
            },
    SHOW("show")
            {
                @Override
                protected boolean executeCommand(String[] args) {
                    if (args.length == 0) {
                        collection.printElements();
                        return true;
                    }
                    else if ((args.length == 1) && (args[0].equals("-json")))
                    {
                        System.out.println(collection.getJsonFromCollection());
                        return true;
                    }
                    else return false;
                }

                @Override
                protected void printUsage() {
                    System.out.println("show\n"
                            + "show -json");
                }
            },
    ADD("add")
            {
                @Override
                protected boolean executeCommand(String[] args) {
                    if (args.length == 1)
                    {
                        if (args[0].contains("-ifmax"))
                        {
                            args = args[0].split(" ", 2);
                            if (collection.compareToMax(args[1]) > 1)
                                addElement(args[1]);
                            else
                                System.out.println("Element is not bigger than "
                                        + "the existing ones and was not added");
                            return true;
                        }
                        else
                        {
                            addElement(args[0]);
                            return true;
                        }
                    }
                    return false;
                }

                private void addElement(String json)
                {
                    try {
                        collection.addCharacter(json);
                        System.out.println("Element successfully added");
                    }
                    catch (JsonSyntaxException | JsonCharacterNameNotGiven e)
                    {
                        System.out.println("Incorrect JSON syntax: " + e.getMessage());
                    }
                }

                @Override
                protected void printUsage() {
                    System.out.println("add <element>\n"
                            + "add -ifmax <element>");
                }
            },
    REMOVE("remove")
            {
                @Override
                protected boolean executeCommand(String[] args) {
                    if (args.length == 1)
                    {
                        removeElement(args[0]);
                        return true;
                    }
                    return false;
                }

                private void removeElement(String json)
                {
                    try {
                        collection.removeCharacter(json);
                    }
                    catch (JsonSyntaxException | JsonCharacterNameNotGiven e) {
                        System.out.println("Incorrect JSON syntax: " + e.getMessage());
                    }
                }

                @Override
                protected void printUsage() {
                    System.out.println("remove <element>");
                }
            },
    EXIT("exit")
            {
                @Override
                protected boolean executeCommand(String[] args) {
                    if (args.length == 0)
                    {
                        collection = null;
                        return true;
                    }
                    return false;
                }

                @Override
                protected void printUsage() {
                    System.out.println("exit");
                }
            },
    LOAD("load")
            {
                @Override
                protected boolean executeCommand(String[] args) {
                    if (args.length == 1)
                    {
                        collection.loadFromFile(args[0]);
                        return true;
                    }
                    return false;
                }

                @Override
                protected void printUsage() {
                    System.out.println("load <file path>");
                }
            },
    SAVE("save")
            {
                @Override
                protected boolean executeCommand(String[] args) {
                    if (args.length == 0)
                    {
                        collection.saveToFile();
                        return true;
                    }
                    else if (args.length == 1)
                    {
                        collection.saveToFile(args[0]);
                        return true;
                    }
                    return false;
                }

                @Override
                protected void printUsage() {
                    System.out.println("save \n"
                            + "save <file path>");
                }
            };



    public static CollectionHandler collection;

    public static void execute(String command, String... args)
    {
        if (collection == null)
            throw new RuntimeException("No CollectionHandler had been given");

        Commands c = findCommand(command);
        if (c != null)
        {
            if (!c.executeCommand(args))
            {
                System.out.println("Correct usage:");
                c.printUsage();
            }
        }
        else
            System.out.println("Unknown command. Use \"help\" to see the list of commands");
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
    protected abstract boolean executeCommand(String[] args);

    protected abstract void printUsage();
}
