#!/bin/bash

# Number of brand pages to create
num_brands=$1

brands=(
    "Apple" "Samsung" "Microsoft" "Sony" "Nike" 
    "Adidas" "KitchenAid" "Lego" "Hasbro" "Starbucks" 
    "Amazon" "Google" "Pfizer" "Johnson & Johnson" "Disney" 
    "Toyota" "Fisher-Price" "Mercedes-Benz" "Canon" "Nikon" 
    "Chanel" "Gucci" "Louis Vuitton" "IKEA" "Versace" 
    "Bose" "Dell" "Logitech" "Panasonic" "Philips"
)

# Shuffle brands and select the first $num_brands
IFS=$'\n' selected_brands=($(printf "%s\n" "${brands[@]}" | shuf -n $num_brands))

# Create BrandPage.csv and add headers
echo -e "\nCreating $num_brands brand pages..."
echo "URL,Name,Description" > BrandPage.csv

# For each brand page to create
for ((i=0; i<$num_brands; i++)); do
    # Create a URL and a name
    url="www.421market.com/$(echo "${selected_brands[i]}" | tr '[:upper:]' '[:lower:]' | tr ' ' '-')"
    name="${selected_brands[i]}"

    # Assign ~20% NULL descriptions and ~80% a description
    if (( (i+1) % 5 == 0 )); then
        description="NULL"
    else
        description="This is the description for ${selected_brands[i]}."
    fi

    # Write the row to BrandPage.csv
    echo "$url,$name,$description" >> BrandPage.csv
done

echo "Generated 'BrandPage.csv' with $(( $(wc -l < BrandPage.csv) - 1 )) rows."

# Max number of admins per brand page
max_manages=$2

echo -e "\nAssigning each brand page 1-$max_manages admins..."

# Create Manages.csv and add headers
echo "UserID,BrandPage,Since,ClearanceLevel" > Manages.csv

# Get all admin IDs into array 'admins' and shuffle them
mapfile -t admins < <(tail -n +2 Admin.csv | cut -d, -f1)
readarray -t admins < <(printf '%s\n' "${admins[@]}" | shuf)

# Get all brand page URLs into array 'brand_pages'
mapfile -t brand_pages < <(tail -n +2 BrandPage.csv | cut -d, -f1)

# Declare an associative array to store existing relationships
declare -A existingRelationships

j=0

# Function to get the next random admin ID
next_admin () {
    if (( j < ${#admins[@]} - 1 )); then
        j=$((j + 1))
    else
        readarray -t admins < <(printf '%s\n' "${admins[@]}" | shuf)
        j=0
    fi
}

# For each brand page
for ((i=0; i<$num_brands; i++)); do
    # Random number of admins for this brand page
    num_manages=$((RANDOM % $max_manages+1))

    for ((k=0; k<$num_manages; k++)); do
        while [[ -n "${existingRelationships["${admins[j]},${brand_pages[i]}"]}" ]]; do
            next_admin
        done

        # The first admin always has clearance level 5
        if (( k == 0 )); then
            clearance=5
        else
            # The rest have a random clearance level
            clearance=$((RANDOM % 5 + 1))
        fi

        # Generate a random date in the past 10 years
        since=$(date -d "$((RANDOM % 3653 + 1)) days ago" +'%Y-%m-%d')

        # Write the row to Manages.csv and add the relationship to the associative array
        echo "${admins[j]},${brand_pages[i]},$since,$clearance" >> Manages.csv
        existingRelationships["${admins[j]},${brand_pages[i]}"]=1

        # Get the next random admin ID
        next_admin
    done
done

echo -e "Generated 'Manages.csv' with $(( $(wc -l < Manages.csv) - 1 )) rows.\n"