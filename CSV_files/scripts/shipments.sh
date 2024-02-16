#!/bin/bash

num_shippers=$1
products_per_shipment=$2

echo "ShipmentNo,ShipperName,OrderID,ModelID,SerialNo,DeliverDate,ShipDate" > Shipment.csv

# Filter Product.csv for rows with non-NULL OrderIDs, shuffle them, and save to filtered_products.csv
awk -F, 'NR > 1 && $4 != "NULL" && $1 != "NULL" && $2 != "NULL"' Product.csv | shuf > filtered_products.csv

# Initialize shipment number and shipper number
shipmentNo=1
shipperNo=1
num_products=$((RANDOM % products_per_shipment + 1))

# Read the filtered_products.csv file line by line
while IFS=, read -r modelID serialNo return orderID supplierName restockNo; do
    # Create a shipment in Shipment.csv
    randomDays=$(( RANDOM % 1096 ))
    shipDate=$(date -I -d "2022-01-01 + $randomDays days")
    deliverDate=$(date -I -d "$shipDate + 3 days")
    echo "$shipmentNo,Shipper$shipperNo,$orderID,$modelID,$serialNo,$deliverDate,$shipDate" >> Shipment.csv

    num_products=$((num_products - 1))

    # Advance to the next shipment number or shipper number
    if ((num_products == 0)); then
        # If the last shipper has been used, reset to the first shipper and increment the shipment number
        if ((shipperNo == num_shippers)); then
            shipperNo=1
            num_products=$((RANDOM % products_per_shipment + 1))
            shipmentNo=$((shipmentNo + 1))
        # Otherwise, increment the shipper number
        else 
            shipperNo=$((shipperNo + 1))
            num_products=$((RANDOM % products_per_shipment + 1))
        fi
    fi
done < filtered_products.csv

# Remove the temporary file
rm filtered_products.csv