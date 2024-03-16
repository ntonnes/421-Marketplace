#!/bin/bash

# Loop over all CSV files in the current directory
for file in *.csv
do
    # Check if file exists
    if [ -f "$file" ]; then
        # Clear the file
        > "$file"
    fi
done

echo
echo "Cleared the contents of all CSV files."