#!/bin/bash

# Check if a command line argument is provided
if [ $# -eq 0 ]
then
    echo "Please provide the number of rows as a command line argument."
    exit 1
fi

X=$1

# Create Restock.csv with headers
echo "SupplierName,RestockNo,Location,Date" > Restock.csv

# Initialize RestockNo for each supplier
declare -A restockNo
for ((i=1; i<=10; i++))
do
    restockNo["Supplier$i"]=1
done

# Fill Restock.csv
for ((i=1; i<=$X; i++))
do
    # Generate random SupplierName and Location
    SupplierName="Supplier$((RANDOM % 10 + 1))"
    Location="Location$((RANDOM % 10 + 1))"

    # Get RestockNo for this supplier and increment it for the next time
    RestockNo=${restockNo[$SupplierName]}
    restockNo[$SupplierName]=$((RestockNo + 1))

    # Generate random date in YYYY-MM-DD format
    Date=$(date -d"$((RANDOM%365)) days ago" +'%Y-%m-%d')

    # Append row to Restock.csv
    echo "$SupplierName,$RestockNo,$Location,$Date" >> Restock.csv
done
