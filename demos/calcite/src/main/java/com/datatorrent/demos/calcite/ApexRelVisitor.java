package com.datatorrent.demos.calcite;

import com.datatorrent.api.DAG;
import com.datatorrent.api.Operator;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelVisitor;

/**
 * Created by fwh492 on 6/13/16.
 */
public class ApexRelVisitor extends RelVisitor {
  DAG dag;

  @Override
  public void visit(RelNode node, int ordinal, RelNode parent) {

  }


}
