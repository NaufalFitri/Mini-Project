package Entities;

public abstract class Entity {

    protected int id;
    protected String name;

    public Entity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public abstract String getName();
    public abstract int getId();

}
