package com.datatorrent.demos.calcite;

import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.api.annotation.InputPortFieldAnnotation;
import com.datatorrent.api.annotation.OutputPortFieldAnnotation;
import com.datatorrent.common.util.BaseOperator;

/**
 * Created by fwh492 on 6/13/16.
 */
public class ApexRelOperator extends BaseOperator {

  public void log(String tuple) {
    System.out.println(this.getClass().getName() + " -> " + tuple);
  }


  public final transient DefaultInputPort<String> inputPort = new DefaultInputPort<String>() {
    @Override
    public void process(String tuple)
    {
      log(tuple);
      outputPort.emit(tuple);
    }
  };

  public final transient DefaultOutputPort<String> outputPort = new DefaultOutputPort<String>(){

  };
}
