package com.visionrent.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.visionrent.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	Boolean existByEmail(String email);
	
	@EntityGraph(attributePaths = "roles")
	Optional<User> findByEmail(String email);

}
