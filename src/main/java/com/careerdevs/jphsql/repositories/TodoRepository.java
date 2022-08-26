package com.careerdevs.jphsql.repositories;

import com.careerdevs.jphsql.models.TodoModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<TodoModel, Integer> {
}