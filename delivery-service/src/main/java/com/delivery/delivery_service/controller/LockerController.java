package com.delivery.delivery_service.controller;

import com.delivery.delivery_service.entity.ParcelLocker;
import com.delivery.delivery_service.repository.LockerCompartmentRepository;
import com.delivery.delivery_service.repository.ParcelLockerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lockers")
public class LockerController {

    private final ParcelLockerRepository lockerRepository;
    private final LockerCompartmentRepository compartmentRepository;

    public LockerController(ParcelLockerRepository lockerRepository,
                            LockerCompartmentRepository compartmentRepository) {
        this.lockerRepository = lockerRepository;
        this.compartmentRepository = compartmentRepository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllLockers() {
        List<ParcelLocker> lockers = lockerRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (ParcelLocker locker : lockers) {
            long slobodni = compartmentRepository
                    .findByLockerIdAndIsOccupiedFalse(locker.getId()).size();

            Map<String, Object> map = new HashMap<>();
            map.put("id", locker.getId());
            map.put("locationName", locker.getLocationName());
            map.put("address", locker.getAddress());
            map.put("latitude", locker.getLatitude());
            map.put("longitude", locker.getLongitude());
            map.put("totalCompartments", locker.getTotalCompartments());
            map.put("availableCompartments", slobodni);
            map.put("active", locker.getActive());
            response.add(map);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParcelLocker> getLockerById(@PathVariable Long id) {
        return lockerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}