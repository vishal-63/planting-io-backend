package com.plantingio.server.Repo;

import com.plantingio.server.Model.Order;
import com.plantingio.server.Model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OrderDetailsRepo extends JpaRepository<OrderDetails, Integer> {

    List<OrderDetails> findByOrderId (int orderId);

    @Query("select o from OrderDetails o where o.nurseryId = ?1 and o.productId is not null")
    List<OrderDetails> findByNurseryId (int nurseryId);

    @Query("select o from OrderDetails o where o.orderId = ?1 and o.nurseryId = ?2")
    List<OrderDetails> findByOrderIdAndNurseryId (int orderId, int nurseryId);


    @Query("select o from OrderDetails o where o.nurseryId = :nurseryId group by orderId")
    List<OrderDetails> findRowsWithUniqueOrderId(int nurseryId);

}
