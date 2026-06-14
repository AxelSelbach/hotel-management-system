package br.com.hotel.model;

public enum RoomStatus {
    AVAILABLE("available"),
    OCCUPIED("occupied");

    private final String dbValue;

    RoomStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static RoomStatus fromString(String value) {
        for (RoomStatus status : values()) {
            if (status.dbValue.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + value);
    }
}
