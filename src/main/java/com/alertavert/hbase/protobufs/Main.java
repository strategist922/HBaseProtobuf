/**
 *
 */
package com.alertavert.hbase.protobufs;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.log4j.Logger;

import com.alertavert.receipts.model.proto.ReceiptsProtos;
import com.alertavert.receipts.model.proto.ReceiptsProtos.ReceiptProto;

/**
 * HBase sample code to experiment with the Java API
 * The `test` table contains protobufs modeling Receipts objects, the rowkey is made of
 * the concatenation of the username and the receipt ID:
 *
 * <pre>auser@gmail.com:1234abce</pre>
 *
 * <p>The column family {@code `cf`} contains only one column ({@code `receipt`}), whose
 * value is the {@link ReceiptsProtos.ReceiptProto} object
 *
 * @author Marco Massenzio (m.massenzio@gmail.com)
 *
 */
public class Main {

  private static final Logger log = Logger.getLogger(Main.class);
  private final Scan scan = new Scan();
  private HTable table;
  private final String user;
  private final String id;
  private boolean success = false;

  public Main(String user, String id) {
    this.user = user;
    this.id = id;
    log.debug("Accessing HBase for user " + user + " [" + id + "]");
    this.success = false;
    Configuration conf = HBaseConfiguration.create();
    try {
      log.debug("Accessing HBase table " + Constants.TABLE);
      table = new HTable(conf, Constants.TABLE);
      if (table == null) {
        log.error(Messages.getString("htable_null"));
        return;
      }
    } catch (IOException e) {
      log.error(Messages.getString("htable_io_ex", e.getMessage()));
      return;
    } catch (Exception ex) {
      log.error(Messages.getString("execute_ex", ex.getLocalizedMessage()));
      return;
    }
    this.success = true;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
      // TODO(marco): extract user and receipt ID from args[]
      String user = Messages.getString("user"); //$NON-NLS-1$
      String id = Messages.getString("id"); //$NON-NLS-1$
      Main app = new Main(user, id);
      if (! app.success) {
        log.error(Messages.getString("cannot_initialize_msg"));
        System.exit(1);
      }
      app.execute();
  }

  public void execute() {
    try {
      log.info(Messages.getString("msg", user)); //$NON-NLS-1$
      ReceiptsProtos.ReceiptProto r = findReceipt();
      log.info(Messages.getString("found_msg", r.getName(), "" + r.getAmount(), r.getCurrency(),
              r.getMerchant()));
      log.info(Messages.getString("scan_tbl_msg", table.getTableDescriptor().getNameAsString())); //$NON-NLS-1$
      // TODO(marco): use the Get object to find an exact row, and then a partial match?
      scanTable();
    } catch (Exception e) {
      log.error(Messages.getString("execute_ex", e.getLocalizedMessage()));
    } finally {
      try {
        if (table != null) table.close();
      } catch (IOException e) {
        // ignore
      }
    }
  }

  /**
   * @param user
   * @param receipt
   * @throws IOException
   */
  public void saveReceipt(ReceiptsProtos.ReceiptProto receipt)
          throws IOException {
    log.info(Messages.getString("save_rcpt_msg", receipt.getId(), Constants.TABLE, user)); //$NON-NLS-1$
    if (receipt.getId().isEmpty()) {
      throw new IllegalArgumentException(Messages.getString("invalid_id_ex_msg")); //$NON-NLS-1$
    }
    Put data = new Put(user.getBytes());
    data.add(Constants.COLUMN_FAMILY.getBytes(), receipt.getId().getBytes(), receipt.toByteArray());
    table.put(data);
  }

  // DIY approach, not very efficient:
  public ReceiptProto findReceipt() throws IOException {
    ResultScanner scanner = table.getScanner(Constants.COLUMN_FAMILY.getBytes(), id.getBytes());
    Result res;
    while ((res = scanner.next()) != null) {
      String rowKey = new String(res.getRow());
      if (rowKey.startsWith(user)) {
        byte[] bytes = res.getValue(Constants.COLUMN_FAMILY.getBytes(), id.getBytes());
        ReceiptsProtos.ReceiptProto r = ReceiptsProtos.ReceiptProto.parseFrom(bytes);
        log.info(Messages.getString("found_msg", r.getName(), "" + r.getAmount(), r.getCurrency(),
            r.getMerchant())); //$NON-NLS-1$
        return r;
      }
    }
    return null;
  }

  private void scanTable() throws Exception {
    scan.addFamily(Constants.COLUMN_FAMILY.getBytes());
    ResultScanner scanner = table.getScanner(scan);
    Result res;
    while ((res = scanner.next()) != null) {
      List<KeyValue> kvs = res.list();
      for(KeyValue kv : kvs) {
        String key = new String(kv.getRow());
        String column = new String(kv.getQualifier());
        String value = new String(kv.getValue());
        log.info(String.format(Messages.getString("row_key_msg"), key, column, value)); //$NON-NLS-1$
      }
    }
  }
}
