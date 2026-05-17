package com.shopease.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopease.ecommerce.model.User;


@Repository
public interface UserRepository extends JpaRepository<User,Long>{

	 Optional<User> findByEmail(String email);
	 boolean existsByEmail(String email);
}
