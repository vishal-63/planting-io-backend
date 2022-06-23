package com.plantingio.server.Repo;

import com.plantingio.server.Model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepo extends JpaRepository<Feedback, Integer> {

    @Query("select f from Feedback f where f.userId = ?1 and f.productId = ?2")
    Feedback findByUserIdAndProductId (int UserId, int productId);

    @Query("select f from Feedback f where f.userId = ?1 and f.serviceId = ?2")
    Feedback findByUserIdAndServiceId (int UserId, int serviceId);

    List<Feedback> findByProductId (int productId);

    List<Feedback> findByServiceId (int serviceId);
}
