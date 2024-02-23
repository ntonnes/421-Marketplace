#!/bin/bash

echo "Assigning $1% of products to orders and shipments..."

# Read the data from the CSV files into arrays
mapfile -t products < <(tail -n +2 Product.csv | shuf)
mapfile -t shipments < <(tail -n +2 Shipment.csv)
mapfile -t orders < <(tail -n +2 Order.csv)

# Calculate the number of products to assign
num_products=${#products[@]}
num_to_assign=$((num_products * $1 / 100))

# Initialize Purchased.csv
echo "ModelID,SerialNo,OrderID,ShipmentNo,ShipperName" > Purchased.csv

# Assign each shipment and order at least one product
for ((i=0; i<${#shipments[@]} && i<${#orders[@]} && i<num_to_assign; i++)); do
    IFS=',' read -r -a product <<< "${products[$i]}"
    IFS=',' read -r -a shipment <<< "${shipments[$i]}"
    IFS=',' read -r -a order <<< "${orders[$i]}"
    echo "${product[0]},${product[1]},${order[0]},${shipment[0]},${shipment[1]}" >> Purchased.csv
done

# Assign the remaining products to random shipments and orders
for ((i=${#shipments[@]}; i<num_to_assign; i++)); do
    IFS=',' read -r -a product <<< "${products[$i]}"
    IFS=',' read -r -a shipment <<< "${shipments[$RANDOM % ${#shipments[@]}]}"
    IFS=',' read -r -a order <<< "${orders[$RANDOM % ${#orders[@]}]}"
    echo "${product[0]},${product[1]},${order[0]},${shipment[0]},${shipment[1]}" >> Purchased.csv
done

echo -e "Generated 'Purchased.csv' with $(( $(wc -l < Purchased.csv) - 1 )) rows.\n"