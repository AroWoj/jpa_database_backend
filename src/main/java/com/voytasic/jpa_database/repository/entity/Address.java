package com.voytasic.jpa_database.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    private String street;
    private String city;
    private String zipcode;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;


}
