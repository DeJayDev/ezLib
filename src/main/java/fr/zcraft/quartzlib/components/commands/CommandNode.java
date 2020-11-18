package fr.zcraft.quartzlib.components.commands;

abstract class CommandNode {
    private final String name;
    private final CommandGroup parent;

    protected CommandNode(String name, CommandGroup parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public CommandGroup getParent() {
        return parent;
    }

    abstract void run(Object instance, String[] args);
}