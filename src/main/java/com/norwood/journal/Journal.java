package com.norwood.journal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.norwood.Room;

public class Journal 
{
    private static Journal instance;
    List<Record> records = new CopyOnWriteArrayList<>();

    private Journal () {
        if (instance != null) {
            throw new RuntimeException("Can't be more than one instance");
        }
    }

    public static Journal getInstance() {
        if (instance == null) {
            synchronized (Journal.class) {
                if (instance == null) {
                    instance = new Journal();
                }
            }
        }
        return instance;
    }
    
    private void addRecord(Record record) {
        System.out.println(record.toString());
        records().add(record);
    }

    public List<Record> records() {
        return records;
    }

    public List<Record> roomRecords(String roomName) {
        return records().stream().filter(r -> r.ofRoom(roomName)).toList();
    }

    public void render() {
        records().forEach(System.out::println);
    }

    public void renderRoom(Room room) {
        throw new RuntimeException("Non impl.");
    }

    public void addRoomRecord(String user, String context, String content) {
        addRecord(new Record(now(), user, context, content));
    }

    public void addServerRecord(String content) {
        addRecord(new Record(now(), "Server", "Server", content));
    }

    private String now() {
        return LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("MM/dd/yyyy/HH:ss:SSS")
        );
    }

    public void clear() {
        records = new CopyOnWriteArrayList<>();
    }
}
