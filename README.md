# DataRobot Stacking Model Demo

This project demonstrates how to use DataRobot's prediction API to implement a stacking model approach, where the predictions from one model are used as features for another model.

## Project Structure

- `StackingDRDemo.java`: Main demo program that shows how to:
  - Load and run the first-stage model from `modelA.jar`
  - Use its predictions as input for the second-stage model from `modelB.jar`
  - Display predictions from both models

- `InspectModels.java`: Utility program to inspect the contents of model JAR files, showing:
  - Model IDs
  - Required features
  - Predictor classes
  - Model information

## Requirements

- Java 8 or higher
- DataRobot prediction API JAR files
- Model JAR files (`modelA.jar` and `modelB.jar`)

## Usage

1. Compile the Java files:
```bash
javac -cp "models/modelA.jar" *.java
```

2. Run the stacking demo:
```bash
java -cp ".:models/modelA.jar:models/modelB.jar" StackingDRDemo
```

3. Inspect model contents:
```bash
java -cp ".:models/modelA.jar:models/modelB.jar" InspectModels
```

## Model Structure

The project uses two model JAR files:
- `modelA.jar`: Contains both first-stage and second-stage models
- `modelB.jar`: Contains the same models as `modelA.jar`

The first-stage model uses basic features:
- `age`
- `current_balance`
- `days_since_last_decrease`
- `utilization_ratio`
- `region_code`

The second-stage model uses all features from the first stage plus:
- `bureau_score`
- `bureau_delinq_cnt`
- `bureau_open_accts`
- `pred_A` (prediction from the first-stage model) 