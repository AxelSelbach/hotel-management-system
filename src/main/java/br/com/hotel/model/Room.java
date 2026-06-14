package br.com.hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private Long id;
    private String roomNumber;
    private RoomType type;
    private double dailyRate;
    private RoomStatus status;
}