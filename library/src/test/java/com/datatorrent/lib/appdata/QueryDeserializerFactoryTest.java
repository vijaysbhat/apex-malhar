/*
 *  Copyright (c) 2012-2015 Malhar, Inc.
 *  All Rights Reserved.
 */

package com.datatorrent.lib.appdata;

import com.datatorrent.lib.appdata.qr.DataDeserializerFactory;
import com.datatorrent.lib.appdata.schemas.SchemaQuery;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Timothy Farkas: tim@datatorrent.com
 */
public class QueryDeserializerFactoryTest
{
  @Test
  public void testMalformedQuery()
  {
    @SuppressWarnings("unchecked")
    DataDeserializerFactory qdf = new DataDeserializerFactory(SchemaQuery.class);

    String malformed = "{\"}";
    boolean exception = false;

    try {
      qdf.deserialize(malformed);
    }
    catch(IOException e) {
      exception = true;
    }

    Assert.assertTrue("Resulting query should throw IOException.", exception);
  }

  @Test
  public void testUnregisteredQueryType()
  {
    @SuppressWarnings("unchecked")
    DataDeserializerFactory qdf = new DataDeserializerFactory(SchemaQuery.class);

    String unsupportedQuery = "{\"id\":\"1\",\"type\":\"Invalid type\"}";
    boolean exception = false;

    try {
      qdf.deserialize(unsupportedQuery);
    }
    catch(IOException e) {
      exception = true;
    }

    Assert.assertTrue("Resulting query should be null.", exception);
  }
}