package com.alikian.flowaggservice.contoller;

import com.alikian.flowaggservice.domain.Flow;
import com.alikian.flowaggservice.service.FlowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class FlowController {

    private final FlowService flowService;

    public FlowController(FlowService flowService) {
        this.flowService = flowService;
    }

    @PostMapping("/flows")
    public ResponseEntity<Void> addFlows(@RequestBody List<Flow> flows) {
        flowService.addFlows(flows);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/flows")
    public ResponseEntity<List<Flow>> readFlows(@RequestParam int hour) {
        List<Flow> flows = flowService.getFlows(hour);
        return ResponseEntity.ok(flows);
    }
}
