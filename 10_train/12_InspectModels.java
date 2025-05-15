import com.datarobot.prediction.IPredictorInfo;
import com.datarobot.prediction.Predictors;

import java.util.Iterator;

public class InspectModels {
    public static void main(String[] args) {
        inspectJar("modelA.jar");
        System.out.println("\n" + "=".repeat(50) + "\n");
        inspectJar("modelB.jar");
        System.out.println("\n" + "=".repeat(50) + "\n");
        inspectJar("modelB2.jar");
    }
    
    private static void inspectJar(String jarName) {
        System.out.println("Inspecting models in " + jarName + "...\n");
        
        Iterator<IPredictorInfo> predictors = Predictors.getAllPredictorsFromJarFile("models/" + jarName);
        int modelCount = 0;
        
        while (predictors.hasNext()) {
            modelCount++;
            IPredictorInfo predictorInfo = predictors.next();
            
            System.out.println("=== Model " + modelCount + " ===");
            System.out.println("Model ID: " + predictorInfo.getModelId());
            System.out.println("Required Features: " + predictorInfo.getFeatures().keySet());
            System.out.println("Predictor Class: " + predictorInfo.getPredictorClass().getName());
            System.out.println("Model Info: " + predictorInfo.getModelInfo());
            System.out.println();
        }
        
        System.out.println("Total models found in " + jarName + ": " + modelCount);
    }
} 