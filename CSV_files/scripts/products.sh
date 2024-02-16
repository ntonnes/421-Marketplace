#!/bin/bash

# Get the max number of products per model from the script argument
max_products_per_model=$1

# Clear the file and add headers
echo "ModelID,SerialNo,Return,OrderID,SupplierName,RestockNo" > Product.csv

# Get all model IDs
mapfile -t modelIDs < <(tail -n +2 Model.csv | cut -d, -f1)

# Get total number of models
total_models=${#modelIDs[@]}

# Initialize counter for processed models
processed_models=0

# Get all restock rows
mapfile -t restockRows < <(tail -n +2 Restock.csv)

# For each model ID
for modelID in "${modelIDs[@]}"
do
    # Initialize the serial number counter to 1 for each model
    nextSerialNo=100000001

    # Generate a random number of products for this model ID
    num_products=$((RANDOM % (max_products_per_model)))

    # For each product to create
    for ((i=1; i<=$num_products; i++))
    do
        # Get a random restock row
        restockRow=${restockRows[$RANDOM % ${#restockRows[@]}]}

        # Get the supplier name and restock number from the restock row
        supplierName=$(echo $restockRow | cut -d, -f1)
        restockNo=$(echo $restockRow | cut -d, -f2)

        # Write the row to Product.csv
        echo "$modelID,$nextSerialNo,NULL,NULL,$supplierName,$restockNo" >> Product.csv

        # Increment the next serial number
        ((nextSerialNo++))
    done

    # Increment counter for processed models
    processed_models=$((processed_models + 1))

    # Calculate percentage of progress as a floating-point number and convert it to an integer
    percent=$(awk "BEGIN {printf \"%.0f\", 100 * $processed_models / $total_models}")

    # Print percentage of progress
    printf "\rProgress: %d%%" $percent
done
echo