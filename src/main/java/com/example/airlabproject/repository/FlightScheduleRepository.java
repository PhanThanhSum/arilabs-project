package com.example.airlabproject.repository;

import com.example.airlabproject.entity.FlightSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface FlightScheduleRepository extends JpaRepository<FlightSchedule, Long> {

    List<FlightSchedule> findByDepIataAndCreatedAtAfter(String depIata, LocalDateTime time);

    void deleteByDepIata(String depIata);
}
