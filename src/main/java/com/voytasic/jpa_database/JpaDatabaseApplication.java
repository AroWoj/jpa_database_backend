package com.voytasic.jpa_database;

import com.voytasic.jpa_database.repository.AddressRepository;
import com.voytasic.jpa_database.repository.UserRepository;
import com.voytasic.jpa_database.role.Role;
import com.voytasic.jpa_database.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;


//@ComponentScan("application")
//@EnableJpaRepositories("healthchecker")
//@EntityScan("healthchecker")

@SpringBootApplication
//@EnableJpaRepositories(basePackages="com.voytasic.jpa_database.repository")
@EnableJpaAuditing
@EnableAsync
public class JpaDatabaseApplication {

	@Bean
	public CommandLineRunner commandLineRunner(RoleRepository roleRepository) {

		return args -> {

//			User arek = new User("Arek", 55);
//			Address arekAddress1 = new Address("Moniuszki 16A", "Pajęczno","98-330");
//			Address arekAddress2 = new Address("Janki","Janki 27", "98-330");
//
//			arekAddress1.setUser(arek);
//			arekAddress2.setUser(arek);
//
//			arek.addAddress(arekAddress1);
//			arek.addAddress(arekAddress2);
//
//			userRepository.save(arek);
//
//			User darek = new User("Darek", 45);
//			Address darekAddress1 = new Address("Kościuszki 99", "Katowice","42-100");
//			darekAddress1.setUser(darek);
//
//			darek.addAddress(darekAddress1);
//
//			userRepository.save(darek);
			if (roleRepository.findByName("USER").isEmpty()) {
				roleRepository.save(
						Role.builder().name("USER").build()
				);
			}

		};
	}

	public static void main(String[] args) {
		SpringApplication.run(JpaDatabaseApplication.class, args);
	}

//
//		};
//	}


}
