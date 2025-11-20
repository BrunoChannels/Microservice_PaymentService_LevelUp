package levelUp_backEnd.levelup.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
public class HealthController {
    
    @RequestMapping("/health")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public String healthCheck() {
        return "Payment Service is up and running!";
    }
    
}
