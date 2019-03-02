package io.bryma.betim.swine.piglet;

import eu.smartsocietyproject.pf.TaskResult;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class PigletTaskResult extends TaskResult {

    private List<String> humanResult = new ArrayList<>();
    private List<String> softwareResult = new ArrayList<>();
    private double qor;
    private QualityAssurance qualityAssurance;

    public PigletTaskResult(QualityAssurance qualityAssurance){
        this.qualityAssurance = qualityAssurance;
    }

    @Override
    public String getResult() {
        return toString();
    }

    public void addHumanResult(String humanResult){
        this.humanResult.add(humanResult);
    }

    public void addSoftwareResult(String softwareResult){
        this.softwareResult.add(softwareResult);
    }

    @Override
    public double QoR() {
        return this.qor;
    }

    public void setQor(double qor){
        this.qor = qor;
    }

    @Override
    public boolean isQoRGoodEnough() {
        return qualityAssurance.isQoRGoodEnough();
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        if(!this.humanResult.isEmpty()){
            stringBuilder.append("Human result: \n");
            this.humanResult.forEach(humanResult -> stringBuilder.append(humanResult).append(", \n"));
        }


        if(!softwareResult.isEmpty()){
            stringBuilder.append("Software result: \n");
            this.softwareResult.forEach(softwareResult -> stringBuilder.append(softwareResult).append(", \n"));
        }

        return stringBuilder.toString();
    }
}
