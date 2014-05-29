package labb3;

import javax.swing.tree.*;

public class MyNode extends DefaultMutableTreeNode {

    private final String name;
    private final String level;
    private final String description;

    public MyNode(String name, String level, String description) {
        super(name);
        this.name = name;
        this.level = level;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getLvl() {                //Skriv inte över getLevel;
        return level;
    }

    public String getDescription() {
        return description;
    }
}