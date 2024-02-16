#!/bin/bash

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

echo "Assigning each brand pages a random level 5 admin..."

# Clear the file and add headers
echo "UserID,BrandPage,Since,ClearanceLevel" > Manages.csv

# Get all admins
mapfile -t admins < <(tail -n +2 Admin.csv | cut -d, -f1)

# Get all brand pages
mapfile -t brand_pages < <(tail -n +2 BrandPage.csv | cut -d, -f1)

# Shuffle admins and brand_pages arrays
readarray -t admins < <(printf '%s\n' "${admins[@]}" | shuf)
readarray -t brand_pages < <(printf '%s\n' "${brand_pages[@]}" | shuf)

# Assign level 5 admins
for ((i=0; i<${#admins[@]} && i<${#brand_pages[@]}; i++)); do
    # Generate a random date in the format YYYY-MM-DD
    since=$(date -d "$((RANDOM % 3653 + 1)) days ago" +'%Y-%m-%d')
    echo "${admins[i]},${brand_pages[i]},$since,5" >> Manages.csv
done

echo "Generated 'BrandPage.csv' with $(( $(wc -l < BrandPage.csv) - 1 )) rows."
echo
echo "Assigning each admin 0-3 additional brand pages with a random clearance level..."

# Read Admin.csv into an array, excluding the header
mapfile -t admins < <(tail -n +2 Admin.csv)

# Get total number of admins
total_admins=${#admins[@]}

# Initialize counter for processed admins
processed_admins=0

# Load existing relationships into an associative array
declare -A existingRelationships
while IFS= read -r line; do
    existingRelationships["$line"]=1
done < Manages.csv

# For each admin
for admin in "${admins[@]}"
do
    # Get UserID
    userID=$(echo "$admin" | tr -d '[:space:]')

    # Generate a random number of rows to create (0-3)
    num_rows=$((RANDOM % 4))

    # For each row to create
    for ((i=0; i<$num_rows; i++))
    do
        # Get a random brand page from BrandPage.csv, excluding the header
        brandPage=$(tail -n +2 BrandPage.csv | shuf -n 1 | cut -d',' -f1)

        # Check if the admin-brand relationship already exists
        if [ -z "${existingRelationships["$userID,$brandPage"]}" ]; then
            # Generate a random clearance level (1-5)
            clearanceLevel=$((RANDOM % 5 + 1))

            # Generate a random date in the format YYYY-MM-DD
            since=$(date -d "$((RANDOM % 3653 + 1)) days ago" +'%Y-%m-%d')

            # Write the row to Manages.csv
            echo "$userID,$brandPage,$since,$clearanceLevel" >> Manages.csv

            # Add the new relationship to the array
            existingRelationships["$userID,$brandPage"]=1
        fi
    done

    # Increment counter for processed admins
    processed_admins=$((processed_admins + 1))

    # Calculate percentage of progress as a floating-point number and convert it to an integer
    percent=$(awk "BEGIN {printf \"%.0f\", 100 * $processed_admins / $total_admins}")

    # Print percentage of progress
    printf "\rProgress: %d%%" $percent
done
echo