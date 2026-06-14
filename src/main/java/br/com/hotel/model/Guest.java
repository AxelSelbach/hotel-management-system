package br.com.hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guest {
    private Long id;
    private String name;
    private String cpf;
    private String phone;
    private String email;
}
