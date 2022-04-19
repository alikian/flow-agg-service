package com.alikian.flowaggservice.service;

import com.alikian.flowaggservice.domain.Flow;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FlowService {

    final Map<Integer, Map<String, Flow>> aggAllHoursFlowMap = new ConcurrentHashMap<>();

    public void addFlows(List<Flow> flows) {
        for (Flow flow : flows) {
            int hour = flow.getHour();
            String key = flow.getKey();

            Map<String, Flow> aggOneHourFlowMap = aggAllHoursFlowMap.computeIfAbsent(hour, k -> {
                Map<String, Flow> newAggOneHourFlowMap = new ConcurrentHashMap<>();
                newAggOneHourFlowMap.put(key, flow);
                return newAggOneHourFlowMap;
            });

            Flow aggFlow = aggOneHourFlowMap.computeIfAbsent(key, k -> flow);
            if (aggFlow != flow) {
                aggFlow.addTransfer(flow);
            }
        }
    }

    public List<Flow> getFlows(int hour) {
        Map<String, Flow> aggFlowMap = aggAllHoursFlowMap.get(hour);
        if (aggFlowMap != null) {
            return new ArrayList<>(aggFlowMap.values());
        } else {
            return new ArrayList<>();
        }
    }
}
