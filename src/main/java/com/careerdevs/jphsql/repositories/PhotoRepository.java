package com.careerdevs.jphsql.repositories;

import com.careerdevs.jphsql.models.PhotoModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<PhotoModel, Integer> {
}
