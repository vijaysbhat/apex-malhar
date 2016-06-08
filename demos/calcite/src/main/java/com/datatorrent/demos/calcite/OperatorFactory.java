package com.datatorrent.demos.calcite;

/**
 * Created by fwh492 on 6/13/16.
 */
public class OperatorFactory {
  // public constructor, per factory contract
  public OperatorFactory() {
  }

  public static ApexRelOperator create(String name) {
    if ("LogicalDelta".equals(name)) {
      return new LogicalDeltaOperator();
    } else if ("LogicalTableScan".equals(name)) {
      return new LogicalTableScanOperator();
    } else if ("LogicalProject".equals(name)) {
      return new LogicalProjectOperator();
    }
    return null;
  }
}
