#!/bin/bash

# Get the number of rows for each user type from the script arguments
num_customers=$1
num_members=$2
num_admins=$3
num_guests=$4

# Clear the files and add headers
echo "UserID,DOB,Password,Email,Name" > Customer.csv
echo "UserID,ExpDate,Points" > Member.csv
echo "UserID" > User.csv
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

        # Generate placeholder values
        dob="2000-01-01"
        password="password"
        email="user${user_id}@example.com"
        name="User ${user_id}"
        exp_date="2022-12-31"
        points="100"

        # Write the row to User.csv and the specific user type file
        echo "$user_id" >> User.csv
        if [ "$file_name" == "Customer.csv" ]; then
            echo "$user_id,$dob,$password,$email,$name" >> $file_name
        elif [ "$file_name" == "Member.csv" ]; then
            echo "$user_id,$exp_date,$points" >> $file_name
        else
            echo "$user_id" >> $file_name
        fi

        # If a second file name is provided, write the row to that file as well
        if [ -n "$second_file_name" ]; then
            if [ "$second_file_name" == "Customer.csv" ]; then
                echo "$user_id,$dob,$password,$email,$name" >> $second_file_name
            elif [ "$second_file_name" == "Member.csv" ]; then
                echo "$user_id,$exp_date,$points" >> $second_file_name
            else
                echo "$user_id" >> $second_file_name
            fi
        fi

    done
}


echo "Creating users..."

# Create members (who are also customers)
create_users $num_members 110000000 Member.csv Customer.csv
echo "Generated 'Member.csv' with $(( $(wc -l < Member.csv) - 1 )) rows."

# Create customers
create_users $num_customers 100000000 Customer.csv
echo "Generated 'Customer.csv' with $(( $(wc -l < Customer.csv) - 1 )) rows."

# Create admins
create_users $num_admins 200000000 Admin.csv
echo "Generated 'Admin.csv' with $(( $(wc -l < Admin.csv) - 1 )) rows."

# Create guests
create_users $num_guests 300000000 Guest.csv
echo "Generated 'Guest.csv' with $(( $(wc -l < Guest.csv) - 1 )) rows."
echo "Generated 'User.csv' with $(( $(wc -l < User.csv) - 1 )) rows."