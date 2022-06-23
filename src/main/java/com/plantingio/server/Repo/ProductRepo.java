package com.plantingio.server.Repo;

import com.plantingio.server.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {

    public boolean existsByName(String name);

    @Query("select count(*) from Product p where p.nurseryId = :id and p.name = :name")
    int productExistsOfNursery(String name, int id);

    List<Product> findByNurseryId(Integer id);

    @Query("select p from Product p where p.type = :type order by p.nurseryId")
    List<Product> findByType(String type);
}
