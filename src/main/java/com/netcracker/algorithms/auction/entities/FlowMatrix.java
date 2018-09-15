package com.netcracker.algorithms.auction.entities;

import java.util.ArrayList;
import java.util.List;

import static com.netcracker.algorithms.auction.entities.Flow.createEmptyFlow;

public class FlowMatrix {

    private final int[] supplyArray;
    private final int[] demandArray;

    private final Flow[][] flowMatrix;
    private final Flow[] unusedFlowArray;

    public FlowMatrix(int[] supplyArray,
                      int[] demandArray) {
        this.supplyArray = supplyArray;
        this.demandArray = demandArray;

        this.flowMatrix = createInitalFlowMatrix(this.supplyArray.length, this.demandArray.length);
        this.unusedFlowArray = createEmptyFlowArray(this.demandArray);
    }

    public Flow getFlow(int sourceIndex,
                        int sinkIndex) {
        return flowMatrix[sourceIndex][sinkIndex];
    }

    public Flow getUnusedFlow(int sinkIndex) {
        return unusedFlowArray[sinkIndex];
    }

    public List<Flow> getAvailableFlowList(int sourceIndex) {
        List<Flow> flowList = new ArrayList<>();
        for (int i = 0; i < flowMatrix.length; i++) {
            if(i == sourceIndex){
                continue;
            }
            for (int j = 0; j < flowMatrix[i].length; j++) {
                Flow flow = this.flowMatrix[i][j];
                if (!flow.isEmpty()) {
                    flowList.add(flow);
                }
            }
        }
        for (int j = 0; j < unusedFlowArray.length; j++) {
            Flow flow = unusedFlowArray[j];
            if (!flow.isEmpty()) {
                flowList.add(flow);
            }
        }
        return flowList;
    }

    private static Flow[][] createInitalFlowMatrix(int supplyArrayLength, int demandArraylength) {
        Flow[][] flowMatrix = new Flow[supplyArrayLength][demandArraylength];
        for (int i = 0; i < supplyArrayLength; i++) {
            for (int j = 0; j < demandArraylength; j++) {
                flowMatrix[i][j] = createEmptyFlow(i, j);
            }
        }
        return flowMatrix;
    }

    private static Flow[] createEmptyFlowArray(int[] demandArray) {
        int unusedIndex = -1;
        int length = demandArray.length;
        Flow[] flowArray = new Flow[length];
        for (int j = 0; j < length; j++) {
            int initialVolume = demandArray[j];
            flowArray[j] = new Flow(unusedIndex, j, initialVolume, 0.0);
        }
        return flowArray;
    }
}
