# DataRobot Stacking Model Demo

This project demonstrates how to use DataRobot's prediction API to implement a stacking model approach, where the predictions from one model are used as features for another model.

## Project Structure

```
stacking-poc/
├── 10_train/                    # Training-related files
│   ├── 11_create_demo_data.py   # Script to create demo data
│   ├── 12_InspectModels.java    # Utility to inspect model JARs
│   ├── data/                    # Training data
│   │   ├── projectA_train.csv   # Training data for first model
│   │   └── projectB_train.csv   # Training data for second model
│   └── models/                  # Model JAR files
│       ├── modelA.jar           # First-stage model
│       ├── modelB.jar           # Second-stage model
│       └── modelB2.jar          # Alternative second-stage model
├── 20_predict/                  # Prediction-related files
│   └── StackingDRDemo.java      # Demo program for stacking predictions
├── Pipfile                      # Python dependencies
├── Pipfile.lock                 # Locked Python dependencies
└── README.md                    # This file
```

## Components

### Training Phase (`10_train/`)
- `11_create_demo_data.py`: Creates synthetic training data for both models
- `12_InspectModels.java`: Utility to inspect model JAR files, showing:
  - Model IDs
  - Required features
  - Predictor classes
  - Model information
- `data/`: Contains training datasets
- `models/`: Contains trained model JAR files

### Prediction Phase (`20_predict/`)
- `StackingDRDemo.java`: Main demo program that shows how to:
  - Load and run the first-stage model from `modelA.jar`
  - Use its predictions as input for the second-stage model from `modelB.jar`
  - Display predictions from both models

## Requirements

- Java 8 or higher
- Python 3.7+ (for data generation)
- DataRobot prediction API JAR files
- Model JAR files (in `10_train/models/`)

## Usage

### Setup Python Environment
```bash
pipenv install  # Install Python dependencies
```

### Generate Demo Data
```bash
cd 10_train
pipenv run python 11_create_demo_data.py
```

### Compile Java Files
```bash
cd 20_predict
javac -cp "../10_train/models/modelA.jar" StackingDRDemo.java
```

### Run the Stacking Demo
```bash
java -cp ".:../10_train/models/modelA.jar:../10_train/models/modelB.jar" StackingDRDemo
```

### Inspect Model Contents
```bash
cd 10_train
javac -cp "models/modelA.jar" 12_InspectModels.java
java -cp ".:models/modelA.jar:models/modelB.jar" InspectModels
```

## Model Structure

The project uses two model JAR files:
- `modelA.jar`: Contains both first-stage and second-stage models
- `modelB.jar`: Contains the same models as `modelA.jar`

### First-stage Model Features
- `age`
- `current_balance`
- `days_since_last_decrease`
- `utilization_ratio`
- `region_code`

### Second-stage Model Features
All features from the first stage plus:
- `bureau_score`
- `bureau_delinq_cnt`
- `bureau_open_accts`
- `pred_A` (prediction from the first-stage model) 