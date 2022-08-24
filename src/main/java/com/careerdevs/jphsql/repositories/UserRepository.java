package com.careerdevs.jphsql.repositories;

import com.careerdevs.jphsql.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Integer> {
}
