#!/bin/bash

# Check if argument is provided
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <number_of_rows>"
    exit 1
fi

# Check if argument is a positive integer
if ! [[ $1 =~ ^[0-9]+$ ]]; then
    echo "Error: Argument must be a positive integer"
    exit 1
fi

echo
echo "Generating 'BrandPage.csv' with $1 rows..."

# Get the number of rows from the first script argument
num_rows=$1

# Calculate the number of rows without a description
num_no_description=$((num_rows / 5))

# Create an array of row indices
indices=($(seq 1 $num_rows))

# Shuffle the array
shuffled_indices=($(for i in "${indices[@]}"; do echo "$i"; done | shuf))

# Clear the file and add headers
echo "URL,Name,Description" > BrandPage.csv

# For each row to create
for ((i=1; i<=$num_rows; i++))
do
    # Create a URL and Name for the brand
    url="www.421market.com/brand$i"
    name="Brand $i"

    # If this row is in the first 20% of the shuffled indices
    if [[ " ${shuffled_indices[@]:0:$num_no_description} " =~ " $i " ]]; then
        description="NULL"
    else
        description="This is the description for Brand $i."
    fi

    # Write the row to BrandPage.csv
    echo "$url,$name,$description" >> BrandPage.csv
done


echo "Assigning each admin to 0-4 BrandPages with random clearance levels..."
# Clear the file and add headers
echo "UserID,BrandPage,ClearanceLevel" > Manages.csv

# For each row in Admin.csv, excluding the header
tail -n +2 Admin.csv | while IFS=, read -r userID
do
    # Trim userID
    userID=$(echo "$userID" | tr -d '[:space:]')

    # Generate a random number of rows to create (0-4)
    num_rows=$((RANDOM % 5))

    # For each row to create
    for ((i=0; i<$num_rows; i++))
    do
        # Get a random brand page from BrandPage.csv, excluding the header
        brandPage=$(tail -n +2 BrandPage.csv | shuf -n 1 | cut -d',' -f1)

        # Check if the admin-brand relationship already exists
        if ! grep -q "^$userID,$brandPage," Manages.csv; then
            # Generate a random clearance level (1-5)
            clearanceLevel=$((RANDOM % 5 + 1))

            # Write the row to Manages.csv
            echo "$userID,$brandPage,$clearanceLevel" >> Manages.csv
        fi
    done
done

echo "Generated 'Manages.csv' with $(wc -l < Manages.csv) rows."
echo
echo "Ensuring each BrandPage has at least one admin with clearance level 5..."

# Initialize a counter for the number of rows added
rows_added=0

# Ensure each BrandPage has at least one admin with clearance level 5
tail -n +2 BrandPage.csv | while IFS=, read -r brandPage
do
    # Trim brandPage
    brandPage=$(echo "$brandPage" | cut -d',' -f1 | tr -d '[:space:]')

    # If the brandPage does not have an admin with clearance level 5
    if ! grep -q ",$brandPage,5" Manages.csv; then
        # Keep selecting a random admin until we find one that does not already manage the brand
        while true; do
            # Get a random admin from Admin.csv, excluding the header
            admin=$(tail -n +2 Admin.csv | shuf -n 1 | tr -d '[:space:]')

            # Check if the admin-brand relationship already exists
            if ! grep -q "^$admin,$brandPage," Manages.csv; then
                # Write the row to Manages.csv
                echo "$admin,$brandPage,5" >> Manages.csv

                # Increment the counter
                ((rows_added++))

                break
            fi
        done
    fi
done

echo "Added $rows_added rows to Manages.csv."
echo
