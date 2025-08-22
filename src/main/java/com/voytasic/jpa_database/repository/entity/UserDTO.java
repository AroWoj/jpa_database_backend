package com.voytasic.jpa_database.repository.entity;

import java.util.Set;

public record UserDTO(Long id, String email, String name, int age, Set<AddressDTO> addresses) {
}
