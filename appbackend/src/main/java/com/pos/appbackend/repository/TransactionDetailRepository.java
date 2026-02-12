package com.pos.appbackend.repository;

import com.pos.appbackend.model.TransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TransactionDetailRepository extends JpaRepository<TransactionDetail, Integer> {

}
