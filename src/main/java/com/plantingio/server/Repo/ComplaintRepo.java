package com.plantingio.server.Repo;

import com.plantingio.server.Model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintRepo extends JpaRepository<Complaint, Integer> {
}
