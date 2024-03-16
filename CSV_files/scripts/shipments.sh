#!/bin/bash

num_shipments=$1
echo creating $num_shipments shipments...

declare -A shippers
shippers=(
    ["Canada Post"]=1 ["Purolator"]=1 ["FedEx Canada"]=1 ["UPS Canada"]=1
    ["Canpar"]=1 ["Loomis Express"]=1 ["DHL Canada"]=1 ["TST Overland Express"]=1
    ["Dicom"]=1 ["Day & Ross"]=1 ["Mackie Moving Systems"]=1 ["Manitoulin Transport"]=1
    ["Cardinal Couriers"]=1 ["Nationex"]=1 ["Intelcom Express"]=1
)

echo "ShipmentNo,ShipperName,DeliverDate,ShipDate" > Shipment.csv

# Convert the keys of the associative array to a regular array
shipperNames=("${!shippers[@]}")

for ((i=1; i<=$num_shipments; i++)); do
    # Select a random shipper
    shipperName=${shipperNames[$RANDOM % ${#shipperNames[@]}]}

    # Create a shipment in Shipment.csv
    randomDays=$(( RANDOM % 1096 ))
    shipDate=$(date -I -d "2022-01-01 + $randomDays days")
    deliverDate=$(date -I -d "$shipDate + $((RANDOM % 8 + 1)) days")
    echo "${shippers[$shipperName]},$shipperName,$deliverDate,$shipDate" >> Shipment.csv

    # Increment the shipment number for the selected shipper
    shippers[$shipperName]=$((shippers[$shipperName] + 1))
done
echo -e "Generated 'Shipment.csv' with $(( $(wc -l < Shipment.csv) - 1 )) rows.\n"