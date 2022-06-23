package com.plantingio.server.Repo;

import com.plantingio.server.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    public Optional<User> findByEmail (String email);

    @Query("select u.id from User u where u.email = :email")
    public Integer findIdByEmail(String email);

    @Query("select u.email from User u where u.id = :id")
    public String findEmailById(int id);

    @Query("select u from User u where u.is_admin = false")
    List<User> findAllUsers();
}
