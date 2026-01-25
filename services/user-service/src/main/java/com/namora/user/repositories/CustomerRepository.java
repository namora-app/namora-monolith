package com.namora.user.repositories;

import com.namora.user.entities.Customer;
import com.namora.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Customer findByUser(User user);
}
