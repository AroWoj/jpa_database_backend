package com.voytasic.jpa_database.controller;

import com.voytasic.jpa_database.repository.entity.Address;
import com.voytasic.jpa_database.repository.entity.AddressDTO;
import com.voytasic.jpa_database.repository.entity.User;
import com.voytasic.jpa_database.repository.entity.UserDTO;
import com.voytasic.jpa_database.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@CrossOrigin(origins = {"http://localhost:4200","http://localhost:80","http://192.168.0.184:80"})
@RestController
//@Transactional
public class UserController {

    @Autowired
    private MainService service;



    @GetMapping("/users")
    public ResponseEntity<Set<UserDTO>> findAllUsers() {
        return service.findAllUsers();
    }

    @GetMapping("/addresses")
    public ResponseEntity<Set<AddressDTO>> findAllAddresses() {
        return service.findAllAddresses();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> findUserById(@PathVariable(name="id") Long id) {
        return service.findUserById(id);
    }

//    @PostMapping("/users")
//    public ResponseEntity<UserDTO> addUser(@RequestBody User user) {
//        return service.addUser(user);
//    }
    @PatchMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable(name="id") Long id, @RequestBody User user) {
        return service.updateUser(id, user);
    }

    @PostMapping("/users/{id}/address")
    public ResponseEntity<UserDTO> addUserAddress(@PathVariable(name="id") Long id, @RequestBody Address address) {
        return service.addUserAddress(id, address);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeUserById(@PathVariable(name="id") Long id){
        service.removeUserById(id);
    }

}
