package com.delivery.delivery_service.config;

import com.delivery.delivery_service.entity.Branch;
import com.delivery.delivery_service.entity.LockerCompartment;
import com.delivery.delivery_service.entity.ParcelLocker;
import com.delivery.delivery_service.repository.BranchRepository;
import com.delivery.delivery_service.repository.LockerCompartmentRepository;
import com.delivery.delivery_service.repository.ParcelLockerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final BranchRepository branchRepository;
    private final ParcelLockerRepository lockerRepository;
    private final LockerCompartmentRepository compartmentRepository;

    public DataSeeder(BranchRepository branchRepository,
                      ParcelLockerRepository lockerRepository,
                      LockerCompartmentRepository compartmentRepository) {
        this.branchRepository = branchRepository;
        this.lockerRepository = lockerRepository;
        this.compartmentRepository = compartmentRepository;
    }

    @Override
    public void run(String... args) {
        seedBranches();
        seedLockers();
    }

    private void seedBranches() {
        if (branchRepository.count() > 0) {
            log.info("Branch tabela vec ima podatke. Preskacem seed.");
            return;
        }

        List<Branch> branches = List.of(
                Branch.builder()
                        .name("Poslovnica Beograd")
                        .address("Knez Mihajlova 5, Beograd")
                        .latitude(44.8178)
                        .longitude(20.4569)
                        .workingHours("08:00-20:00")
                        .phone("+381111234567")
                        .build(),
                Branch.builder()
                        .name("Poslovnica Novi Sad")
                        .address("Bulevar Oslobodjenja 10, Novi Sad")
                        .latitude(45.2551)
                        .longitude(19.8451)
                        .workingHours("08:00-20:00")
                        .phone("+381211234567")
                        .build(),
                Branch.builder()
                        .name("Poslovnica Nis")
                        .address("Obrenoviceva 15, Nis")
                        .latitude(43.3209)
                        .longitude(21.8958)
                        .workingHours("08:00-20:00")
                        .phone("+381181234567")
                        .build()
        );

        branchRepository.saveAll(branches);
        log.info("Ubaceno {} poslovnica", branches.size());
    }

    private void seedLockers() {
        if (lockerRepository.count() > 0) {
            log.info("ParcelLocker tabela vec ima podatke. Preskacem seed.");
            return;
        }

        List<ParcelLocker> lockers = List.of(
                ParcelLocker.builder()
                        .locationName("Paketomat Centar")
                        .address("Trg Republike 1, Beograd")
                        .latitude(44.8163)
                        .longitude(20.4607)
                        .totalCompartments(20)
                        .build(),
                ParcelLocker.builder()
                        .locationName("Paketomat Novi Beograd")
                        .address("Bulevar Mihajla Pupina 2, Beograd")
                        .latitude(44.8195)
                        .longitude(20.4095)
                        .totalCompartments(15)
                        .build(),
                ParcelLocker.builder()
                        .locationName("Paketomat Novi Sad")
                        .address("Futoska 20, Novi Sad")
                        .latitude(45.2671)
                        .longitude(19.8335)
                        .totalCompartments(25)
                        .build(),
                ParcelLocker.builder()
                        .locationName("Paketomat Nis")
                        .address("Vozda Karadjordja 5, Nis")
                        .latitude(43.3217)
                        .longitude(21.8955)
                        .totalCompartments(10)
                        .build(),
                ParcelLocker.builder()
                        .locationName("Paketomat Zemun")
                        .address("Glavna 30, Zemun")
                        .latitude(44.8456)
                        .longitude(20.4098)
                        .totalCompartments(20)
                        .build()
        );

        List<ParcelLocker> savedLockers = lockerRepository.saveAll(lockers);
        log.info("Ubaceno {} paketomata", savedLockers.size());

        for (ParcelLocker locker : savedLockers) {
            List<LockerCompartment> compartments = new ArrayList<>();
            for (int i = 1; i <= locker.getTotalCompartments(); i++) {
                compartments.add(LockerCompartment.builder()
                        .lockerId(locker.getId())
                        .compartmentNumber(i)
                        .isOccupied(false)
                        .build());
            }
            compartmentRepository.saveAll(compartments);
            log.info("Ubaceno {} sanducica za paketomat {}", compartments.size(), locker.getLocationName());
        }
    }
}