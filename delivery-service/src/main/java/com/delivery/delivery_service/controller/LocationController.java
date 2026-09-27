package com.delivery.delivery_service.controller;

import com.delivery.delivery_service.entity.Branch;
import com.delivery.delivery_service.entity.ParcelLocker;
import com.delivery.delivery_service.repository.BranchRepository;
import com.delivery.delivery_service.repository.LockerCompartmentRepository;
import com.delivery.delivery_service.repository.ParcelLockerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final BranchRepository branchRepository;
    private final ParcelLockerRepository lockerRepository;
    private final LockerCompartmentRepository compartmentRepository;

    public LocationController(BranchRepository branchRepository,
                              ParcelLockerRepository lockerRepository,
                              LockerCompartmentRepository compartmentRepository) {
        this.branchRepository = branchRepository;
        this.lockerRepository = lockerRepository;
        this.compartmentRepository = compartmentRepository;
    }

    @GetMapping("/nearby")
    public ResponseEntity<Map<String, Object>> getNearby(
            @RequestParam(defaultValue = "44.8") Double lat,
            @RequestParam(defaultValue = "20.4") Double lng,
            @RequestParam(defaultValue = "1000") Double radius) {

        List<Map<String, Object>> branches = new ArrayList<>();
        for (Branch b : branchRepository.findAll()) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", b.getId());
            m.put("name", b.getName());
            m.put("address", b.getAddress());
            m.put("latitude", b.getLatitude());
            m.put("longitude", b.getLongitude());
            m.put("workingHours", b.getWorkingHours());
            m.put("phone", b.getPhone());
            m.put("active", b.getActive());
            branches.add(m);
        }

        List<Map<String, Object>> lockers = new ArrayList<>();
        for (ParcelLocker l : lockerRepository.findAll()) {
            long slobodni = compartmentRepository
                    .findByLockerIdAndIsOccupiedFalse(l.getId()).size();

            Map<String, Object> m = new HashMap<>();
            m.put("id", l.getId());
            m.put("locationName", l.getLocationName());
            m.put("address", l.getAddress());
            m.put("latitude", l.getLatitude());
            m.put("longitude", l.getLongitude());
            m.put("totalCompartments", l.getTotalCompartments());
            m.put("availableCompartments", slobodni);
            m.put("active", l.getActive());
            lockers.add(m);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("branches", branches);
        response.put("lockers", lockers);
        return ResponseEntity.ok(response);
    }
}