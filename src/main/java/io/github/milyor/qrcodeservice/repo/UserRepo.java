package io.github.milyor.qrcodeservice.repo;

import io.github.milyor.qrcodeservice.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {

    Optional<Users> findByUsername(String username);;

}
