package br.com.hotel.model;

public enum ReservationStatus {
    CONFIRMED("Confirmada"),
    CHECKED_IN("Em Andamento"),
    CHECKED_OUT("Finalizada"),
    CANCELLED("Cancelada");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ReservationStatus fromString(String value) {
        if (value == null) return CONFIRMED;
        for (ReservationStatus status : values()) {
            if (status.name().equalsIgnoreCase(value) ||
                    status.displayName.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return CONFIRMED;
    }

    @Override
    public String toString() {
        return displayName;
    }
}