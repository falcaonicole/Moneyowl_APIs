package com.finance.moneyowl.restcontroller;

import com.finance.moneyowl.configuration.DBHealthIndicator;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class AppInitializerController {

    private final DBHealthIndicator dbHealthIndicator;

    @GetMapping("/app/start")
    public ResponseEntity<String> ping() {
        Health health = dbHealthIndicator.health();
        String status = "DB is Down";

        if (health.getStatus().equals(Status.UP)) {
            status = "Aplication and DB is Up and Running";
        }
        return ResponseEntity.ok(status);
    }
}
