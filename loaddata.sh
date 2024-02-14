#!/bin/bash

# Empty the loaddata.sql file
> loaddata.sql

# Iterate over each CSV file in the CSV_files directory
for file in CSV_files/*.csv; do
    # Get the table name from the file name
    tablename=$(basename "$file" .csv)

    # Read the CSV file line by line
    while IFS= read -r line; do
        # Convert the line into a SQL insert statement
        values=$(echo "$line" | sed "s/,/','/g" | sed "s/^/('/" | sed "s/$/')/")
        echo "INSERT INTO $tablename VALUES $values;" >> loaddata.sql
    done < "$file"
done

# Load the data into the database - commented out for now
# db2 -t -v < loaddata.sql 2>&1 | tee loaddata.log