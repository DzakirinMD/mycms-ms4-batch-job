package com.mycmsms4batchjob.repository;

import com.mycmsms4batchjob.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}
