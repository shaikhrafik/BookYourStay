package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.pojo.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	
	 Optional<Role> findByName(String role);


	    boolean existsByName(String role);

}
