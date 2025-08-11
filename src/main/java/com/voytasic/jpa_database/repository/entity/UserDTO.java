package com.voytasic.jpa_database.repository.entity;

import java.util.Set;

public record UserDTO(String name, int age, Set<AddressDTO> addresses) {
}
