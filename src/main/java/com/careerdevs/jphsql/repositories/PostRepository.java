package com.careerdevs.jphsql.repositories;

import com.careerdevs.jphsql.models.PostModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostModel, Integer> {
}
