package com.bonappetit.repo;

import com.bonappetit.model.entity.Recipe;
import com.bonappetit.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   Optional<User> findByUsernameAndEmail(String username, String email);
   Optional<User> findByUsernameOrEmail(String username, String email);

   Optional<User> findByUsername(String username);

}
