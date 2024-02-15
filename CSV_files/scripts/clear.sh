#!/bin/bash

# Loop over all CSV files in the current directory
for file in *.csv
do
    # Skip Categories.csv
    if [ "$file" == "Categories.csv" ]; then
        continue
    fi

    # Check if file exists
    if [ -f "$file" ]; then
        # Clear the file
        > "$file"
    fi
done

echo "Cleared all CSV files in the current directory except Categories.csv."