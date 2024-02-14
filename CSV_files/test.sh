#!/bin/bash

# Read BrandPage.csv into an array
mapfile -t brandPages < BrandPage.csv

# Get the number of brand pages
numBrandPages=${#brandPages[@]}

# Remove the header row
unset brandPages[0]

# For each row in Admin.csv
tail -n +2 Admin.csv | while IFS=, read -r userID
do
    # For each new row to be added
    for _ in {1..2}
    do
        # Select a random brand page
        brandPage=${brandPages[$RANDOM % $numBrandPages]}

        # Extract the URL from the brand page
        url=$(echo "$brandPage" | cut -d, -f1)

        # Generate random values for the other columns
        since=$(date -d"$((RANDOM % 3653 + 1)) days ago" +%Y-%m-%d)
        clearanceFlag=$((RANDOM % 2))

        # Add the new row to Manages.csv
        echo "$userID,$url,$since,$clearanceFlag" >> Manages.csv
    done
done