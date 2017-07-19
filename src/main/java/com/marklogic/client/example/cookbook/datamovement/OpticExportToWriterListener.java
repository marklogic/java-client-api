package com.marklogic.client.example.cookbook.datamovement;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.datamovement.Batch;
import com.marklogic.client.datamovement.BatchFailureListener;
import com.marklogic.client.datamovement.DataMovementException;
import com.marklogic.client.datamovement.QueryBatch;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.Plan;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;

/**
 * An extension of OpticExportListener which facilitates writing all row records
 * from the constructed Plan to a single Writer output stream. The Writer could
 * be a FileWriter, for example, to write output to a CSV file.
 */
public class OpticExportToWriterListener extends OpticExportListener {
  private static Logger logger = LoggerFactory.getLogger(OpticExportToWriterListener.class);
  private Writer writer;
  private String suffix;
  private String prefix;
  private List<OpticOutputListener> outputListeners = new ArrayList<>();

  public OpticExportToWriterListener(Function<QueryBatch, Plan> function, RowManager rowManager, Writer writer) {
    super(function, rowManager);
    this.writer = writer;
  }

  /**
   * Sets the string suffix to append to the writer after each record.
   *
   * @param suffix the string suffix
   * @return this instance for method chaining
   */
  public OpticExportToWriterListener withRecordSuffix(String suffix) {
    this.suffix = suffix;
    return this;
  }

  /**
   * Sets the string prefix to send to the writer before each record.
   *
   * @param prefix the string prefix
   * @return this instance for method chaining
   */
  public OpticExportToWriterListener withRecordPrefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  /**
   * Registers a custom listener to override the default behavior for each
   * record which sends the record contents in a JSON format to the writer. This
   * listener can choose what string to send to the writer for each document.
   *
   * @param listener the custom listener (or lambda expression)
   * @return this instance (for method chaining)
   */
  public OpticExportToWriterListener onGenerateOutput(OpticOutputListener listener) {
    outputListeners.add(listener);
    return this;
  }

  /**
   * The listener interface required by onGenerateOutput.
   */
  public static interface OpticOutputListener {
    /**
     * Given the RowRecord, generate the desired String output to send to the
     * writer.
     *
     * @param record the RowRecord retrieved from the server
     * @return the String output to send to the writer
     */
    public String generateOutput(RowRecord record);
  }

  @Override
  public void processEvent(QueryBatch batch) {
    try {
      PlanBuilder.Plan exportPlan = exportFunction.apply(batch);
      synchronized (writer) {
        for (RowRecord record : rowManager.resultRows(exportPlan)) {
          try {
            if (prefix != null)
              writer.write(prefix);
            if (outputListeners.size() > 0) {
              for (OpticOutputListener listener : outputListeners) {
                String output = null;
                try {
                  output = listener.generateOutput(record);
                } catch (Throwable t) {
                  logger.error("Exception thrown by an onGenerateOutput listener", t);
                }
                if (output != null)
                  writer.write(output);
              }
            } else {
              writer.write(record.toString());
            }
            if (suffix != null)
              writer.write(suffix);
          } catch (IOException e) {
            throw new DataMovementException("Failed to write the Optic API records", e);
          }
        }
      }
    } catch (Throwable t) {
      for (BatchFailureListener<Batch<String>> listener : getFailureListeners()) {
        try {
          listener.processFailure(batch, t);
        } catch (Throwable t2) {
          logger.error("Exception thrown by an onBatchFailure listener", t2);
        }
      }
    }
  }
}