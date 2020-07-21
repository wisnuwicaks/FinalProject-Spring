package com.cimb.finalproject.dao;

import com.cimb.finalproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Integer> {
    @Query(value = "SELECT * FROM user WHERE username= ?1 and password = ?2", nativeQuery = true)
    public User findUserLogin(String username, String password);

    @Query(value = "SELECT * FROM user WHERE username= ?1", nativeQuery = true)
    public Iterable<User> findAllByUsername(String username);

    @Query(value = "SELECT * FROM user WHERE email = ?1", nativeQuery = true)
    public Iterable<User> findAllByEmail(String email);

    @Query(value = "SELECT * FROM user WHERE id = ?1 and verification_code=?2", nativeQuery = true)
    public Optional<User> getUserToReset (int id, String verif_code);

    public Optional<User> findByEmail(String email);
    public Optional<User> findByUsername(String username);
}
