package com.datatorrent.demos.calcite;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.linq4j.function.Function0;
import org.apache.calcite.rel.RelCollations;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelProtoDataType;
import org.apache.calcite.schema.*;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.ImmutableBitSet;

import java.util.Iterator;
import java.util.Random;

/**
 * Created by fwh492 on 6/13/16.
 */
public class OrderStream implements ScannableTable, StreamableTable {
  protected final RelProtoDataType protoRowType = new RelProtoDataType() {
    public RelDataType apply(RelDataTypeFactory a0) {
      return a0.builder()
              .add("rowtime", SqlTypeName.TIMESTAMP)
              .add("id", SqlTypeName.INTEGER)
              .add("product", SqlTypeName.VARCHAR, 10)
              .add("units", SqlTypeName.INTEGER)
              .build();
    }
  };

  public static final Function0<Object[]> ROW_GENERATOR =
          new Function0<Object[]>() {
            private int counter = 0;
            private Random rnd = new Random();
            private Iterator<String> items =
                    Iterables.cycle("paint", "paper", "brush").iterator();

            @Override public Object[] apply() {
              return new Object[]{System.currentTimeMillis(), counter++, items.next(), rnd.nextInt(20) + 1};
            }
          };

  public Enumerable<Object[]> scan(DataContext root) {
    return Linq4j.asEnumerable(new Iterable<Object[]>() {
      @Override
      public Iterator<Object[]> iterator() {
        return new Iterator<Object[]>() {
          public boolean hasNext() {
            return true;
          }

          public Object[] next() {
            return ROW_GENERATOR.apply();
          }

          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }
    });
  }

  public RelDataType getRowType(RelDataTypeFactory typeFactory) {
    return protoRowType.apply(typeFactory);
  }

  public Statistic getStatistic() {
    return Statistics.of(100d,
            ImmutableList.<ImmutableBitSet>of(),
            RelCollations.createSingleton(0));
  }
  public Schema.TableType getJdbcTableType() {
    return Schema.TableType.TABLE;
  }

  @Override
  public Table stream() {
    return this;
  }
}
