package com.plantingio.server.Repo;

import com.plantingio.server.Model.Nursery;
import com.plantingio.server.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NurseryRepo extends JpaRepository<Nursery, Integer> {
    public Nursery findByEmail (String email);

    @Query("select n.id from Nursery n where n.email = :email")
    public Integer findIdByEmail(String email);

    @Query("select n.name from Nursery n where n.id = :id")
    String findNameById(Integer id);
}
