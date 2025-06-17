package tn.portfolio.axon.common.controller;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class ReplayController {

    private final EventProcessingConfiguration processingConfiguration;

    public ReplayController(EventProcessingConfiguration processingConfiguration) {
        this.processingConfiguration = processingConfiguration;
    }

    @PostMapping("/replay/{projectionName}")
    public ResponseEntity<String> replayProjectProjection(@PathVariable String projectionName) {
        processingConfiguration.eventProcessor(projectionName, TrackingEventProcessor.class)
                .ifPresent(tep -> tep.shutDown());

        processingConfiguration.eventProcessor(projectionName, TrackingEventProcessor.class)
                .ifPresent(tep -> {
                    tep.resetTokens();
                    tep.start();
                });

        return ResponseEntity.ok("Replay of "+projectionName+" started");
    }
}