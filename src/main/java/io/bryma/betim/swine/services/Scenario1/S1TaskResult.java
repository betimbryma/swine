package io.bryma.betim.swine.services.Scenario1;

import eu.smartsocietyproject.pf.TaskResult;

public class S1TaskResult extends TaskResult {

    private String result;
    private double qos;
    private double qosStep = 0.25;

    public S1TaskResult(String result, double qos, double qosStep) {
        this.result = result;
        this.qos = qos;
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public double QoR() {
        return qos > 1 ? 1 : qos;
    }

    @Override
    public boolean isQoRGoodEnough() {
        return qos >= qosStep;
    }

    public void setHumanResult(String result){
        this.result = "Human opinion: "
                + System.getProperty("line.separator")
                + result
                + System.getProperty("line.separator");
        this.qos += qosStep;
    }
}