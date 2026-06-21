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
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        // Tenta várias formas (maiúscula, minúscula, original)
        String upper = value.trim().toUpperCase();
        String lower = value.trim().toLowerCase();

        for (RoomType type : values()) {
            if (type.name().equals(upper) ||
                    type.dbValue.equalsIgnoreCase(value) ||
                    type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }

        System.err.println("⚠️ Tipo de quarto não reconhecido: " + value);
        throw new IllegalArgumentException("Tipo de quarto inválido: " + value);
    }

    @Override
    public String toString() {
        return dbValue;
    }
}