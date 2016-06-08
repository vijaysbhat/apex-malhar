/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.datatorrent.demos.calcite;

import com.datatorrent.api.DAG;
import com.datatorrent.api.StreamingApplication;
import com.datatorrent.api.annotation.ApplicationAnnotation;
import com.datatorrent.lib.io.ConsoleOutputOperator;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.RelVisitor;
import org.apache.calcite.schema.*;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.tools.*;
import org.apache.hadoop.conf.Configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;


/**
 * @since 2.1.0
 */
@ApplicationAnnotation(name="CalciteDemo")
public class Application implements StreamingApplication
{
  @Override
  public void populateDAG(DAG dag, Configuration conf)
  {
    try {
      //load the driver
      Class.forName("org.apache.calcite.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }


    //create schema for words
    SchemaPlus rootSchema = Frameworks.createRootSchema(true);
    rootSchema.add("streams", new OrdersSchema());

    for (String t: rootSchema.getSubSchema("streams").getTableNames()) {
      System.out.println(t);
    }

    String sql = "SELECT STREAM " +
              "\"product\" " +
            "FROM (SELECT \"product\", \"units\" FROM \"streams\".\"orders\") p";
//    SqlParser parser = SqlParser.create(sql);
    FrameworkConfig config = Frameworks.newConfigBuilder().defaultSchema(rootSchema).build();
    Planner planner = Frameworks.getPlanner(config);
    SqlNode parse = null;
    try {
      parse = planner.parse(sql);
      SqlNode validate = planner.validate(parse);
      RelRoot tree = planner.rel(validate);
      final LinkedHashMap<String, ApexRelOperator> operators = new LinkedHashMap<>();
      final LinkedHashMap<String, String> streams = new LinkedHashMap<>();
      RelVisitor visitor = new RelVisitor() {
        @Override
        public void visit(RelNode node, int ordinal, RelNode parent) {
          System.out.println("Visiting " + node.toString());
          operators.put(node.toString(), OperatorFactory.create(node.getRelTypeName()));
          streams.put(node.toString(), parent.toString());
          super.visit(node, ordinal, parent);
        }
      };
      OrderStreamOperator input = dag.addOperator("input", new OrderStreamOperator());
      operators.put(tree.rel.toString(), OperatorFactory.create(tree.rel.getRelTypeName()));
      System.out.println("Visiting " + tree.toString());
      tree.rel.childrenAccept(visitor);

      String firstOperatorName = null;
      for (HashMap.Entry<String, ApexRelOperator> o : operators.entrySet()) {
        dag.addOperator(o.getKey(), o.getValue());
        firstOperatorName = o.getKey();
      }

      dag.addStream("input-" + firstOperatorName, input.outputPort,
              operators.get(firstOperatorName).inputPort);
      for (HashMap.Entry<String, String> s : streams.entrySet()) {
        String streamName = s.getKey() + "-" + s.getValue();
        System.out.println("Stream " + streamName);
        dag.addStream(streamName, operators.get(s.getKey()).outputPort,
                operators.get(s.getValue()).inputPort);
      }

      ConsoleOutputOperator consoleOperator = dag.addOperator("console",
              new ConsoleOutputOperator());
      dag.addStream("count-console", operators.get(tree.rel.toString()).outputPort,
              consoleOperator.input);

    } catch (SqlParseException e) {
      e.printStackTrace();
    } catch (RelConversionException e) {
      e.printStackTrace();
    } catch (ValidationException e) {
      e.printStackTrace();
    }
  }
}
