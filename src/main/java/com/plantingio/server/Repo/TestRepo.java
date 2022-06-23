package com.plantingio.server.Repo;

import com.plantingio.server.Model.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepo extends JpaRepository<Test, Integer> {
}
