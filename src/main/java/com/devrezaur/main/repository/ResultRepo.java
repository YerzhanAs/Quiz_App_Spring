package com.devrezaur.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.devrezaur.main.model.Result;

import java.util.List;

@Repository
public interface ResultRepo extends JpaRepository<Result, Integer> {


}
