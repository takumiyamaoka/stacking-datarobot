import com.datarobot.prediction.IClassificationPredictor;
import com.datarobot.prediction.IPredictorInfo;
import com.datarobot.prediction.Predictors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StackingDRDemo {

  public static void main(String[] args) {
    String inputCsvFile = "input/predict_sample.csv";
    String outputCsvFile = "output/predictions.csv";
    
    // Load all models from classpath
    System.out.println("Loading models...");
    Iterator<IPredictorInfo> predictors = Predictors.getAllPredictors();
    if (!predictors.hasNext()) {
      System.err.println("Error: No models found in classpath");
      return;
    }

    // Get first model (first-stage)
    IPredictorInfo modelAInfo = predictors.next();
    System.out.println("Using first-stage model:");
    System.out.println("Model ID: " + modelAInfo.getModelId());
    System.out.println("Required Features: " + modelAInfo.getFeatures().keySet());
    IClassificationPredictor modelA = (IClassificationPredictor) Predictors.getPredictor(modelAInfo.getModelId());

    // Get second model (second-stage)
    if (!predictors.hasNext()) {
      System.err.println("Error: Second model not found");
      return;
    }
    IPredictorInfo modelBInfo = predictors.next();
    System.out.println("\nUsing second-stage model:");
    System.out.println("Model ID: " + modelBInfo.getModelId());
    System.out.println("Required Features: " + modelBInfo.getFeatures().keySet());
    IClassificationPredictor modelB = (IClassificationPredictor) Predictors.getPredictor(modelBInfo.getModelId());

    // Process CSV file
    try (BufferedReader br = new BufferedReader(new FileReader(inputCsvFile));
         BufferedWriter bw = new BufferedWriter(new FileWriter(outputCsvFile))) {
      
      // Write header
      bw.write("row_id,first_stage_score,second_stage_score\n");
      
      String line;
      String[] headers = null;
      int rowCount = 0;

      // Read and process each row
      while ((line = br.readLine()) != null) {
        if (headers == null) {
          headers = line.split(",");
          continue;
        }

        rowCount++;
        String[] values = line.split(",");
        Map<String, Object> rowA = new HashMap<>();
        
        // Create input row for Model A
        for (int i = 0; i < headers.length; i++) {
          if (!headers[i].equals("pred_A") && !headers[i].equals("bad_flag")) {
            try {
              if (headers[i].equals("age") || headers[i].equals("days_since_last_decrease") || 
                  headers[i].equals("bureau_delinq_cnt") || headers[i].equals("bureau_open_accts")) {
                rowA.put(headers[i], Integer.parseInt(values[i]));
              } else if (headers[i].equals("utilization_ratio") || headers[i].equals("current_balance") || 
                        headers[i].equals("bureau_score")) {
                rowA.put(headers[i], Double.parseDouble(values[i]));
              } else {
                rowA.put(headers[i], values[i]);
              }
            } catch (NumberFormatException e) {
              rowA.put(headers[i], values[i]);
            }
          }
        }

        // First stage prediction
        Map<String, Double> modelAPredictions = modelA.score(rowA);
        double predA = modelAPredictions.get("1");

        // Create input row for Model B
        Map<String, Object> rowB = new HashMap<>(rowA);
        rowB.put("pred_A", predA);

        // Second stage prediction
        Map<String, Double> modelBPredictions = modelB.score(rowB);
        double predB = modelBPredictions.get("1");

        // Write predictions to output file
        bw.write(String.format("%d,%.6f,%.6f\n", rowCount, predA, predB));
      }
      
      System.out.println("\nTotal rows processed: " + rowCount);
      System.out.println("Predictions written to: " + outputCsvFile);
      
    } catch (IOException e) {
      System.err.println("Error processing files: " + e.getMessage());
      e.printStackTrace();
    }
  }
} 