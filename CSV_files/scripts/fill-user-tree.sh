#!/bin/bash

# Get the number of rows for each user type from the script arguments
num_customers=$1
num_members=$2
num_admins=$3
num_guests=$4

# Clear the files and add headers
echo
echo "UserID" > User.csv
echo "UserID" > Customer.csv
echo "UserID" > Member.csv
echo "UserID" > Admin.csv
echo "UserID" > Guest.csv

# Function to create users
create_users() {
    local num_users=$1
    local base_user_id=$2
    local file_name=$3

    # For each user to create
    for ((i=1; i<=$num_users; i++))
    do
        # Calculate the user ID for this user
        user_id=$((base_user_id + i))

        # Write the row to User.csv and the specific user type file
        echo "$user_id" >> User.csv
        echo "$user_id" >> $file_name
    done
}

# Create customers
echo
create_users $num_customers 100000000 Customer.csv
echo "Generated 'Customer.csv' with $num_customers rows."
# Create members
create_users $num_members 110000000 Member.csv
echo "Generated 'Member.csv' with $num_members rows."
# Create admins
create_users $num_admins 200000000 Admin.csv
echo "Generated 'Admin.csv' with $num_admins rows."
# Create guests
create_users $num_guests 300000000 Guest.csv
echo "Generated 'User.csv' with $((num_customers + num_members + num_admins + num_guests)) rows."
echo