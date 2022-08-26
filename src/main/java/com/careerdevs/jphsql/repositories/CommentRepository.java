package com.careerdevs.jphsql.repositories;

import com.careerdevs.jphsql.models.CommentModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentModel, Integer> {
}