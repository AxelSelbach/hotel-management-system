package br.com.hotel.model;

public enum RoomType {
    SINGLE("Single"),
    DOUBLE("Double"),
    LUXURY("luxury");

    private final String displayName;

    RoomType(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
