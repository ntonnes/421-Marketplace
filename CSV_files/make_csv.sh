#!/bin/bash

# Read SQL file and extract table names
tables=$(awk -F' ' '/CREATE TABLE/{print $3}' createtbl_updated.sql | sed 's/(//')

# Create an empty CSV file for each table
for table in $tables
do
    touch "${table}.csv"
done