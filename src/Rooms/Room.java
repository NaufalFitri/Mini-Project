package Rooms;

public abstract class Room {

    private final int RoomID;
    private String roomNumber;
    private final int maxEntities;
    private boolean isOccupied;

    public Room(int id, String number, int maxEntities) {
        this.RoomID = id;
        this.roomNumber = number;
        this.maxEntities = maxEntities;
    }

    public String getRoomNumber() { return roomNumber; }

    public int getRoomID() {
        return RoomID;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public void setRoomNumber(String number) { roomNumber = number; }

    public boolean isOccupied() {
        return isOccupied;
    }

    public int getMaxEntities() {return maxEntities; }

}
