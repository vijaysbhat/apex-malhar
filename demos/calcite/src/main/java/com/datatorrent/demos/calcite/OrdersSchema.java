package com.datatorrent.demos.calcite;

import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.schema.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by fwh492 on 6/13/16.
 */
public class OrdersSchema implements Schema {
  @Override
  public Table getTable(String s) {
    if ("orders".equals(s)) {
      return new OrderStream();
    } else {
      return null;
    }
  }

  @Override
  public Set<String> getTableNames() {
    Set<String> tableNames = new HashSet<String>();
    tableNames.add("orders");
    return tableNames;
  }

  @Override
  public Collection<Function> getFunctions(String s) {
    return null;
  }

  @Override
  public Set<String> getFunctionNames() {
    return null;
  }

  @Override
  public Schema getSubSchema(String s) {
    return null;
  }

  @Override
  public Set<String> getSubSchemaNames() {
    return null;
  }

  @Override
  public Expression getExpression(SchemaPlus schemaPlus, String s) {
    return null;
  }

  @Override
  public boolean isMutable() {
    return false;
  }

  @Override
  public boolean contentsHaveChangedSince(long l, long l1) {
    return false;
  }
}
