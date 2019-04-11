package io.bryma.betim.swine.DTO;

import io.bryma.betim.swine.model.Execution;
import io.bryma.betim.swine.model.Piglet;

import java.util.List;

public class PigletDTO {
  private Piglet piglet;
  private List<Execution> cbts;

  public PigletDTO() {
  }

  public PigletDTO(Piglet piglet, List<Execution> cbts) {
    this.piglet = piglet;
    this.cbts = cbts;
  }

  public Piglet getPiglet() {
    return piglet;
  }

  public void setPiglet(Piglet piglet) {
    this.piglet = piglet;
  }

  public List<Execution> getCbts() {
    return cbts;
  }

  public void setCbts(List<Execution> cbts) {
    this.cbts = cbts;
  }
}
