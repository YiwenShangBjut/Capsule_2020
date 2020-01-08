package com.example.weahen.wstest.Model;

public class ChatRoom {
    private String roomId;
    private String roomName;
    private String startTime;
    private String endTime;

    public ChatRoom(String roomId, String roomName, String startTime, String endTime) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.startTime = startTime;
        this.endTime = endTime;
    }



    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
