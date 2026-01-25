package com.namora.user.repositories;

import com.namora.user.entities.Rider;
import com.namora.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiderRepository extends JpaRepository<Rider, String> {
    Rider findByUser(User user);
}
