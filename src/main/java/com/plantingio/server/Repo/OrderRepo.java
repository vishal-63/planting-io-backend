package com.plantingio.server.Repo;

import com.plantingio.server.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Integer> {

    @Query("select (count(o) > 0) from Order o where o.sessionId = ?1")
    boolean existsBySessionId(String sessionId);

    List<Order> findByUserId(int userId);
}
