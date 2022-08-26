package com.careerdevs.jphsql.repositories;

import com.careerdevs.jphsql.models.AlbumModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<AlbumModel, Integer> {
}
