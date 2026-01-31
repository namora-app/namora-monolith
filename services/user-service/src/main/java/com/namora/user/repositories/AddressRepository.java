package com.namora.user.repositories;

import com.namora.user.entities.Address;
import com.namora.user.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    @Query("SELECT a FROM Address a WHERE a.customer = :customer")
    List<Address> findByCustomer(@Param("customer") Customer customer);
}
