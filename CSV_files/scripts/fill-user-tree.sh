#!/bin/bash

# Get the number of rows for each user type from the script arguments
num_customers=$1
num_members=$2
num_admins=$3
num_guests=$4

# Clear the files and add headers
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
    local second_file_name=$4

    # For each user to create
    for ((i=1; i<=$num_users; i++))
    do
        # Calculate the user ID for this user
        user_id=$((base_user_id + i))

        # Write the row to User.csv and the specific user type file
        echo "$user_id" >> User.csv
        echo "$user_id" >> $file_name

        # If a second file name is provided, write the row to that file as well
        if [ -n "$second_file_name" ]; then
            echo "$user_id" >> $second_file_name
        fi
    done
}

# Create customers
create_users $num_customers 100000000 Customer.csv
# Create members (who are also customers)
create_users $num_members 110000000 Member.csv Customer.csv
# Create admins
create_users $num_admins 200000000 Admin.csv
# Create guests
create_users $num_guests 300000000 Guest.csv

# Print the number of rows in each file
echo
echo "Generated 'Customer.csv' with $((num_customers + num_members)) rows."
echo "Generated 'Member.csv' with $num_members rows."
echo "Generated 'Admin.csv' with $num_admins rows."
echo "Generated 'Guest.csv' with $num_guests rows."
echo "Generated 'User.csv' with $((num_customers + num_members + num_admins + num_guests)) rows."
echo