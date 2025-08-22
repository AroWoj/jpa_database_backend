package com.voytasic.jpa_database.mapper;


import com.voytasic.jpa_database.repository.entity.Address;
import com.voytasic.jpa_database.repository.entity.AddressDTO;
import com.voytasic.jpa_database.repository.entity.User;
import com.voytasic.jpa_database.repository.entity.UserDTO;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Mappers {


    public static UserDTO toUserDTO(User user) {
        var addressesDTO = user.getAddresses().stream()
                .map(Mappers::toAddressDTO)
                .collect(Collectors.toSet());
        return new UserDTO(user.getId(), user.getEmail(), user.getName(), user.getAge(), addressesDTO );
    }

    public static AddressDTO toAddressDTO(Address address) {
        return new AddressDTO(address.getStreet(), address.getCity(), address.getZipcode());
    }

}
