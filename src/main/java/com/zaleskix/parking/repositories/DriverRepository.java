package com.zaleskix.parking.repositories;

import com.zaleskix.parking.domain.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver,String> {

    Optional<Driver> findByLicensePlate (String licensePlate);
    Optional<Driver> findByTransactionDay(String transactionDay);
}