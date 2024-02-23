#!/bin/bash

echo "SupplierName,RestockNo,Location,Date" > Restock.csv
num_restocks=$1
num_suppliers=$2
mapfile -t suppliers < <(tail -n +2 scripts/Suppliers.csv | tr -d '\r' | shuf -n $num_suppliers)

echo "Creating $num_restocks restocks using $num_suppliers suppliers..."

# Initialize RestockNo for each supplier
declare -A restockNo
for line in "${suppliers[@]}"; do
    SupplierName=$(echo "$line" | cut -d',' -f1)
    restockNo["$SupplierName"]=1
done

# Fill Restock.csv
for ((i=1; i<=$num_restocks; i++))
do
    # Generate random SupplierName and Location
    index=$((RANDOM % ${#suppliers[@]}))
    line=${suppliers[$index]}
    SupplierName=$(echo "$line" | cut -d',' -f1)
    Location=$(echo "$line" | cut -d',' -f2-3 | tr ',' ' ' | tr -d '\n')

    # Get RestockNo for this supplier and increment it for the next time
    RestockNo=${restockNo[$SupplierName]}
    restockNo[$SupplierName]=$((RestockNo + 1))

    # Generate random date in YYYY-MM-DD format
    Date=$(date -d"$((RANDOM%900)) days ago" +'%Y-%m-%d')

    # Append row to Restock.csv
    echo "$SupplierName,$RestockNo,"$Location",$Date" >> Restock.csv
done
echo -e "Generated 'Restock.csv' with $(( $(wc -l < Restock.csv) - 1 )) rows.\n"