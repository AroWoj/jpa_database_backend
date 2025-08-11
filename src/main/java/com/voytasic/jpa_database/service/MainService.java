package com.voytasic.jpa_database.service;

import com.voytasic.jpa_database.mapper.Mappers;
import com.voytasic.jpa_database.repository.AddressRepository;
import com.voytasic.jpa_database.repository.ActivateTokenRepository;
import com.voytasic.jpa_database.repository.UserRepository;
import com.voytasic.jpa_database.repository.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MainService {

    @Autowired
    public UserRepository userRepository;
    @Autowired
    public AddressRepository addressRepository;
    @Autowired
    public ActivateTokenRepository tokenRepository;

    public ResponseEntity<Set<UserDTO>> findAllUsers() {
        var users = userRepository.findAll()
            .stream()
            .map(Mappers::toUserDTO)
            .collect(Collectors.toSet());
        if (users.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    public ResponseEntity<UserDTO> findUserById(Long id) {
        var userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        var userDTO = Mappers.toUserDTO(userOptional.get());
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    public ResponseEntity<UserDTO> addUser(User user) {
        var userDTO = Mappers.toUserDTO(userRepository.save(user));
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    public ResponseEntity<UserDTO> addUserAddress(Long user_id, Address address) {
        var userOptional = userRepository.findById(user_id);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        var user = userOptional.get();

        address.setUser(user);
        user.getAddresses().add(address);

        var userDTO = Mappers.toUserDTO(user);
        userRepository.save(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    public ResponseEntity<Set<AddressDTO>> findAllAddresses() {
        var addresses = addressRepository.findAll()
                .stream()
                .map(Mappers::toAddressDTO)
                .collect(Collectors.toSet());
        if (addresses.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    public ResponseEntity<UserDTO> removeUserById(Long id) {
        //tokenRepository.deleteByUserId(id);

        var userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        var user = userOptional.get();
        tokenRepository.deleteByUserId(user.getId());
        userRepository.delete(user);
        var userDTO = Mappers.toUserDTO(user);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }
}
