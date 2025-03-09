package com.norwood;

import java.util.ArrayList;
import java.util.List;

public class Room 
{
    private String name;
    private String owner;
    private List<String> users = new ArrayList<>();
    
    public Room(String name, String owner) {
        this.name = name;
        this.owner = owner;
        addUser(owner);
    }
    
    public void addUser(String user) {
        users.add(user);
    }
}
