import com.datarobot.prediction.IClassificationPredictor;
import com.datarobot.prediction.IPredictorInfo;
import com.datarobot.prediction.Predictors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StackingDRDemo {

  public static void main(String[] args) {
    // テストデータ
    Map<String, Object> rowA = new HashMap<>();
    rowA.put("utilization_ratio", 35.36);
    rowA.put("current_balance", 1234.0);
    rowA.put("days_since_last_decrease", 980);
    rowA.put("age", 40);
    rowA.put("region_code", "Kanto");
    rowA.put("bureau_score", 650);
    rowA.put("bureau_delinq_cnt", 1);
    rowA.put("bureau_open_accts", 5);

    // First stage classification model (modelA)
    System.out.println("Loading Model A...");
    Iterator<IPredictorInfo> modelAPredictors = Predictors.getAllPredictorsFromJarFile("../10_train/models/modelA.jar");
    double predA = 0.0;  // Store Model A's Class 1 prediction

    // Get the first model from modelA.jar (first-stage model)
    if (modelAPredictors.hasNext()) {
      IPredictorInfo predictorInfo = modelAPredictors.next();
      String modelId = predictorInfo.getModelId();
      System.out.println("Using first-stage model from modelA.jar:");
      System.out.println("Model ID: " + modelId);
      System.out.println("Required Features: " + predictorInfo.getFeatures().keySet());
      
      IClassificationPredictor modelA = (IClassificationPredictor) Predictors.getPredictor(modelId);
      Map<String, Double> modelAPredictions = modelA.score(rowA);
      System.out.println("\nFirst-stage predictions:");
      for (String class_label : modelAPredictions.keySet()) {
        System.out.printf("Class %s: %f%n", class_label, modelAPredictions.get(class_label));
        if (class_label.equals("1")) {
          predA = modelAPredictions.get(class_label);
        }
      }
    } else {
      System.err.println("Error: No models found in modelA.jar");
      return;
    }

    // Create a new row for Model B with pred_A
    Map<String, Object> rowB = new HashMap<>(rowA);
    rowB.put("pred_A", predA);
    System.out.println("\nAdded pred_A to input: " + predA);

    // Second stage classification model (modelB)
    System.out.println("\nLoading Model B...");
    Iterator<IPredictorInfo> modelBPredictors = Predictors.getAllPredictorsFromJarFile("../10_train/models/modelB.jar");
    
    // Skip the first model and get the second model from modelB.jar (second-stage model)
    if (modelBPredictors.hasNext()) {
      modelBPredictors.next(); // Skip first model
      if (modelBPredictors.hasNext()) {
        IPredictorInfo predictorInfo = modelBPredictors.next();
        String modelId = predictorInfo.getModelId();
        System.out.println("Using second-stage model from modelB.jar:");
        System.out.println("Model ID: " + modelId);
        System.out.println("Required Features: " + predictorInfo.getFeatures().keySet());
        
        IClassificationPredictor modelB = (IClassificationPredictor) Predictors.getPredictor(modelId);
        Map<String, Double> modelBPredictions = modelB.score(rowB);
        System.out.println("\nSecond-stage predictions:");
        for (String class_label : modelBPredictions.keySet()) {
          System.out.printf("Class %s: %f%n", class_label, modelBPredictions.get(class_label));
        }
      } else {
        System.err.println("Error: Second model not found in modelB.jar");
      }
    } else {
      System.err.println("Error: No models found in modelB.jar");
    }
  }
} 