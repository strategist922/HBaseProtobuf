/**
 * 
 */
package com.alertavert.hbase.protobufs;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.alertavert.receipts.model.proto.ReceiptsProtos;

/**
 * @author marco
 *
 */
public class MainTest {

  private static final Logger log = Logger.getLogger(MainTest.class);
  
  private HTable mock;
  
  ReceiptsProtos.ReceiptProto createReceipt(Integer id) {
    return ReceiptsProtos.ReceiptProto.newBuilder()
       .setName("your-receipt")
       .setAmount(99.99f)
       .setCurrency("USD")
       .setId(id.toString())
       .setMerchant("Beautiful Objects")
       .build();
  }
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    mock = createMock(HTable.class);
  }

  @Test
  public void test() {
    log.info("Running a test with a mock");
    assertNotNull(createReceipt(123));
    assertNotNull(mock);
  }

}
