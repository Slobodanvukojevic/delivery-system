package com.delivery.notification_service.config;

import com.delivery.notification_service.entity.EmailTemplate;
import com.delivery.notification_service.repository.EmailTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final EmailTemplateRepository templateRepository;

    public DataSeeder(EmailTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public void run(String... args) {
        if (templateRepository.count() > 0) {
            log.info("EmailTemplate tabela vec ima podatke. Preskacem seed.");
            return;
        }

        List<EmailTemplate> templates = List.of(
                EmailTemplate.builder()
                        .code("LOCKER_ASSIGNED")
                        .language("sr")
                        .subject("Vas paket ceka u paketomatu")
                        .body("Postovani,\n\nVas paket je smesten u paketomat.\nKod za preuzimanje: {{pickupCode}}\n\nRok za preuzimanje: 3 dana.\n\nHvala sto koristite nas servis.")
                        .build(),
                EmailTemplate.builder()
                        .code("LOCKER_ASSIGNED")
                        .language("en")
                        .subject("Your package is waiting at the locker")
                        .body("Dear customer,\n\nYour package has been placed in a parcel locker.\nPickup code: {{pickupCode}}\n\nPickup deadline: 3 days.\n\nThank you for using our service.")
                        .build(),
                EmailTemplate.builder()
                        .code("DELIVERED")
                        .language("sr")
                        .subject("Paket uspesno dostavljen")
                        .body("Postovani,\n\nVas paket je uspesno dostavljen.\n\nHvala sto koristite nas servis.")
                        .build(),
                EmailTemplate.builder()
                        .code("DELIVERED")
                        .language("en")
                        .subject("Package successfully delivered")
                        .body("Dear customer,\n\nYour package has been successfully delivered.\n\nThank you for using our service.")
                        .build(),
                EmailTemplate.builder()
                        .code("RETURN_TO_SENDER")
                        .language("sr")
                        .subject("Paket se vraca posiljaocu")
                        .body("Postovani,\n\nNazalost, paket nije preuzet u predvidjenom roku i bice vracen posiljaocu.\n\nHvala na razumevanju.")
                        .build(),
                EmailTemplate.builder()
                        .code("RETURN_TO_SENDER")
                        .language("en")
                        .subject("Package returning to sender")
                        .body("Dear customer,\n\nUnfortunately, the package was not picked up in time and will be returned to sender.\n\nThank you for understanding.")
                        .build()
        );

        templateRepository.saveAll(templates);
        log.info("Ubaceno {} email sablona", templates.size());
    }
}