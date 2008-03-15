/*
 * Copyright 2007 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
 *
 * @created Feb 25, 2008 
 * @author wseyler
 */


package org.pentaho.experimental.chart.plugin;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URL;

import org.pentaho.experimental.chart.ChartBoot;
import org.pentaho.experimental.chart.core.ChartDocument;
import org.pentaho.experimental.chart.core.parser.ChartXMLParser;
import org.pentaho.experimental.chart.data.ChartTableModel;
import org.pentaho.experimental.chart.plugin.api.ChartResult;
import org.pentaho.experimental.chart.plugin.api.IOutput;

import junit.framework.TestCase;

/**
 * @author wseyler
 *
 */
public class PluginTest extends TestCase {
  
  protected void setUp() throws Exception {
    super.setUp();

    // Boot the charting library - required for parsing configuration
    ChartBoot.getInstance().start();   
  }
  
  public void testValidate() throws Exception {
    ChartXMLParser chartParser = new ChartXMLParser();
    URL chartXmlDocument = this.getClass().getResource("PluginTest.xml");
    ChartDocument chartDocument = chartParser.parseChartDocument(chartXmlDocument);
    if (chartDocument == null) {
      fail("A null document should never be returned");
    }
    
    IChartPlugin chartPlugin = ChartPluginFactory.getChartPlugin();
    ChartResult result = chartPlugin.validateChartDocument(chartDocument);
    assertEquals(result.getErrorCode(), IChartPlugin.RESULT_VALIDATED);
  }
  
  public void testRender() throws Exception {
    IChartPlugin plugin = ChartPluginFactory.getChartPlugin("org.pentaho.experimental.chart.plugin.jfreechart.JFreeChartPlugin"); //$NON-NLS-1$
    IOutput output = ChartPluginFactory.getChartOutput();
    // At this point we have an output of the correct type
    // Now we can manipulate it to meet our needs so that we get the correct
    // output location and type.
    
    // Now get the chart definition
    ChartXMLParser chartParser = new ChartXMLParser();
    URL chartXmlDocument = this.getClass().getResource("PluginTest.xml");
    ChartDocument chartDocument = chartParser.parseChartDocument(chartXmlDocument);
    if (chartDocument == null) {
      fail("A null document should never be returned");
    }
    
    // Now lets create some data
    ChartTableModel data = new ChartTableModel();
    Object[][] dataArray = {{30, 20, 17}, {20, 40, 35}, {46, 35, 86}};
    data.setData(dataArray);
    output.setFilename("test/test-output/TestChart.png");
    output.setFileType(IOutput.FILE_TYPE_PNG);
    
    // Render and save the plot
    plugin.renderChartDocument(chartDocument, data, output);
    ByteArrayOutputStream newOutputStream = (ByteArrayOutputStream) output.getChartAsStream();
    assertTrue(newOutputStream.toByteArray().length > 5000);
  }
}