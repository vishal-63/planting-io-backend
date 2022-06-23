package com.plantingio.server.Repo;

import com.plantingio.server.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Integer> {

    List<Payment> findByNurseryId (int nurseryId);

    List<Payment> findByOrderId (int orderId);

    @Query("select p from Payment p where p.orderId = ?1 and p.nurseryId = ?2")
    Payment findByOrderIdAndNurseryId(int orderId, int nurseryId);
}
