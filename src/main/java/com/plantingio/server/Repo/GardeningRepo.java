package com.plantingio.server.Repo;

import com.plantingio.server.Model.Gardening;
import com.plantingio.server.Model.Product;
import com.plantingio.server.Utility.ServiceResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GardeningRepo extends JpaRepository<Gardening, Integer> {

    List<Gardening> findAll();

    List<Gardening> findByNurseryId(Integer id);

//    public Integer existsByNurseryId(Integer nurseryId);

}
