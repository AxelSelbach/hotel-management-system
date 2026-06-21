package br.com.hotel.model;

public enum RoomType {
    SINGLE("Single"),
    DOUBLE("Double"),
    LUXURY("Luxury");

    private final String dbValue;

    RoomType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static RoomType fromString(String value) {
        if (value == null) return null;
        for (RoomType type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de quarto inválido: " + value);
    }

    @Override
    public String toString() {
        return dbValue;
    }
}