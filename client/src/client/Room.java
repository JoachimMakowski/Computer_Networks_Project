package client;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private String roomName;
    private String[] usersList = null;
    private List<Message> messagesList = new ArrayList<Message>();

    public Room(String roomName, String[] userList, List<Message> messages) {
        this.roomName = roomName;
        this.usersList = userList;
        this.messagesList = messages;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

}
