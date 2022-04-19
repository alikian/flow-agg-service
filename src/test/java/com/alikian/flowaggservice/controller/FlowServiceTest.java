package com.alikian.flowaggservice.controller;

import com.alikian.flowaggservice.contoller.FlowController;
import com.alikian.flowaggservice.domain.Flow;
import com.alikian.flowaggservice.service.FlowService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FlowServiceTest {

    String payload = "[\n" +
            "{\"src_app\": \"foo\", \"dest_app\": \"bar\", \"vpc_id\": \"vpc-0\", \"bytes_tx\": 100, \"bytes_rx\": 300, \"hour\": 1},\n" +
            "{\"src_app\": \"foo\", \"dest_app\": \"bar\", \"vpc_id\": \"vpc-0\", \"bytes_tx\": 200, \"bytes_rx\": 600, \"hour\": 1},\n" +
            "{\"src_app\": \"baz\", \"dest_app\": \"qux\", \"vpc_id\": \"vpc-0\", \"bytes_tx\": 100, \"bytes_rx\": 500, \"hour\": 1},\n" +
            "{\"src_app\": \"baz\", \"dest_app\": \"qux\", \"vpc_id\": \"vpc-0\", \"bytes_tx\": 100, \"bytes_rx\": 500, \"hour\": 2},\n" +
            "{\"src_app\": \"baz\", \"dest_app\": \"qux\", \"vpc_id\": \"vpc-1\", \"bytes_tx\": 100, \"bytes_rx\": 500, \"hour\": 2}\n" +
            "]";

    private FlowService flowService;

    private FlowController flowController;

    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        this.flowService = new FlowService();
        this.flowController = new FlowController(this.flowService);
    }

    @Test
    public void simpleTest() throws InterruptedException {

        int totalThread = 30;
        int repeat = 10000;
        long start = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(totalThread);
        for (int thread = 0; thread < totalThread; thread++) {
            executor.execute(parallelTest(repeat));
        }

        int count = 0;
        executor.shutdown();
        while (!executor.awaitTermination(100, TimeUnit.MILLISECONDS)) {
            System.out.println("Waiting ms " + count * 100);
            count++;
        }

        long end = System.currentTimeMillis();

        List<Flow> aggFlows = flowService.getFlows(1);
        Map<String, List<Flow>> flowsGroup = aggFlows.stream().collect(Collectors.groupingBy(Flow::getKey));

        Flow flowAgg1 = flowsGroup.get("foo|bar|vpc-0").get(0);
        assertThat(flowAgg1.getBytesTx()).isEqualTo(300 * repeat * totalThread);
        assertThat(flowAgg1.getBytesRx()).isEqualTo(900 * repeat * totalThread);

        Flow flowAgg2 = flowsGroup.get("baz|qux|vpc-0").get(0);
        assertThat(flowAgg2.getBytesTx()).isEqualTo(100 * repeat * totalThread);
        assertThat(flowAgg2.getBytesRx()).isEqualTo(500 * repeat * totalThread);

        List<Flow> aggFlows3 = flowService.getFlows(3);
        assertThat(aggFlows3.isEmpty()).isTrue();

        System.out.println("TPS in write: " + totalThread * repeat / (end - start) * 1000);
    }

    private Runnable parallelTest(int repeat) {
        return () -> {
            try {
                for (int i = 0; i < repeat; i++) {
                    List<Flow> flows = mapper.readValue(payload, new TypeReference<List<Flow>>() {
                    });
                    flowService.addFlows(flows);
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        };
    }
}
