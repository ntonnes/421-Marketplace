#!/bin/bash

# Save the original working directory
orig_dir=$(pwd)
cd ./CSV_files/

# Run the refresh-data.sh script
./scripts/refresh.sh

# Create loaddata.sql in the parent directory
echo -e "\nWriting .csv contents into INSERT statements in 'loaddata.sql'..."
echo "CONNECT TO COMP421;" > ../loaddata.sql

# Define the order of CSV files
files=(
    "Category.csv" 
    "BrandPage.csv" 
    "Model.csv" 
    "Belongs.csv" 
    "User.csv" 
    "Customer.csv" 
    "InCart.csv" 
    "Review.csv" 
    "Member.csv" 
    "Coupon.csv" 
    "Admin.csv" 
    "Manages.csv" 
    "Card.csv" 
    "Restock.csv" 
    "Order.csv" 
    "Product.csv" 
    "Shipment.csv"
    "Purchased.csv"
)

# Count total number of CSV files
total_files=${#files[@]}

# Initialize counter for processed files
processed_files=0

# For each CSV file in the order defined
for file in "${files[@]}"
do
    # Get the table name from the file name
    table_name=$(basename "$file" .csv)

    # Read the CSV file into an array, excluding the header
    mapfile -t rows < <(tail -n +2 "$file")

    if [[ $table_name == "Review" ]]; then
        for row in "${rows[@]}"
        do
            echo "$row" | awk -F'"' -v OFS='' '{ gsub(",", "", $2); print $1 $2 $3 }' | awk -F, -v table="$table_name" 'BEGIN{OFS=","} {printf "INSERT INTO " table " VALUES ('\''%s'\'','\''%s'\'','\''%s'\'','\''%s'\'');\n", $1, $2, $3, $4}' >> ../loaddata.sql
        done
    else
        for row in "${rows[@]}"
        do
            # Convert the row to a SQL insert statement and append it to loaddata.sql

            echo "INSERT INTO $table_name VALUES ($(echo "$row" | awk 'BEGIN{FS=OFS=","} {for(i=1;i<=NF;i++) if($i ~ /^".*"$/) gsub(/,/,"",$i)} 1' | sed "s/,/','/g" | sed "s/^/'/" | sed "s/$/'/" | sed "s/'NULL'/NULL/g"));" >> ../loaddata.sql
        done
    fi

    # Increment counter for processed files
    processed_files=$((processed_files + 1))

    # Calculate percentage of progress as a floating-point number and convert it to an integer
    percent=$(awk "BEGIN {printf \"%.0f\", 100 * $processed_files / $total_files}")

    # Print percentage of progress
    printf "\rProgress: %d%%" $percent
done

# Change back to the original working directory
cd "$orig_dir"

echo -e "\nGenerated 'loaddata.sql' successfully."