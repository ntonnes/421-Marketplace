#!/bin/bash

# Check if arguments are provided
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <max_number_of_products_per_model>"
    exit 1
fi

# Check if arguments are positive integers
if ! [[ $1 =~ ^[0-9]+$ ]]; then
    echo "Error: Argument must be a positive integer"
    exit 1
fi

# Get the max number of products per model from the script argument
max_products_per_model=$1

# Clear the file and add headers
echo "ModelID,SerialNo,Return,OrderID,SupplierName,RestockNo" > Product.csv

# Get all model IDs
mapfile -t modelIDs < <(tail -n +2 Model.csv | cut -d, -f1)

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
done

echo "Generated 'Product.csv' with $(wc -l < Product.csv) rows."
echo