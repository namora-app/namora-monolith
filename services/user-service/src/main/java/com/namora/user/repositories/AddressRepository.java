package com.namora.user.repositories;

import com.namora.user.entities.Address;
import com.namora.user.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    List<Address> findByCustomer(Customer customer);
}
