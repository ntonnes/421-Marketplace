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

mapfile -t names < <(tail -n +2 scripts/Names.csv | awk -F ',' '{print $1 " " $2}')
readarray -t names < <(printf '%s\n' "${names[@]}" | shuf)


next_name () {
    if (( j < ${#names[@]} - 1 )); then
        j=$((j + 1))
    else
        readarray -t names < <(printf '%s\n' "${names[@]}" | shuf)
        j=0
    fi
}


make_email() {
    firstname=$(echo $1 | cut -d' ' -f1 | tr '[:upper:]' '[:lower:]')
    lastname=$(echo $1 | cut -d' ' -f2 | tr '[:upper:]' '[:lower:]')
    digit=$(( RANDOM % 5 ))
    name_choice=$(( RANDOM % 2 ))

    if (( name_choice == 0 )); then
        name="${firstname}"
    else 
        name="${lastname}"
    fi

    if (( digit == 4 )); then
        adjectives=("happy" "sad" "angry" "sleepy" "hungry" "thirsty" "fast" "slow" "hot" "cold")
        start="${firstname}.the.${adjectives[$(( RANDOM % ${#adjectives[@]} ))]}"
    elif (( digit == 3 )); then
        nouns=("boss" "king" "queen" "ninja" "chief" "captain" "knight" "prince" "princess" "wizard")
        start="${nouns[$(( RANDOM % ${#nouns[@]} ))]}.${name}"
    elif (( digit == 2 )); then
        start="${firstname}.${lastname}"
    elif (( digit == 1 )); then
        start="${name}$(( RANDOM % 100 ))"
    else
        start="${firstname:0:1}${lastname}$(( RANDOM % 100 ))"
    fi

    domains=(
        "google.com" "yahoo.com" "hotmail.com" 
        "aol.com" "msn.com" "live.com" "outlook.com" 
        "gmail.com" "mail.com" "protonmail.com"
    )
    random_domain=${domains[$(( RANDOM % ${#domains[@]} ))]}

    echo "${start}@${random_domain}"
}


# Function to create users
create_users() {
    local num_users=$1
    local file_name=$2
    local second_file_name=$3

    # For each user to create
    for ((i=1; i<=$num_users; i++))
    do
        # Generate a unique user ID for this user
        while true; do
            user_id=$((100 + RANDOM % 900))$((100 + RANDOM % 900))$((100 + RANDOM % 900))
            if ! grep -q "$user_id" User.csv; then
                break
            fi
        done

        # Write the row to User.csv and the specific user type file
        echo "$user_id" >> User.csv
        if [ "$file_name" == "Customer.csv" ]; then

            next_name
            password=$(tr -dc 'a-zA-Z0-9' < /dev/urandom | fold -w 10 | head -n 1)

            if (( i % 4 == 0 )); then
                dob=NULL
            else
                dob=$(date -d "$(( RANDOM % 365 * 18 + 13140 )) days ago" +'%Y-%m-%d')
            fi

            email=$(make_email "${names[j]}")
            echo "$user_id,$dob,$password,$email,${names[j]}" >> $file_name

        elif [ "$file_name" == "Admin.csv" ]; then
            echo "$user_id" >> $file_name
        fi

        # If a second file name is provided, write the row to that file as well
        if [ -n "$second_file_name" ]; then
            if [ "$second_file_name" == "Member.csv" ]; then

                exp_date=$(date -d "$(( RANDOM % 365 + 1 )) days" +'%Y-%m-%d')
                points=$(( RANDOM % 10001 ))
                echo "$user_id,$exp_date,$points" >> $second_file_name
                
            fi
        fi

    done
    if [ -n "$second_file_name" ]; then
        echo "Generated '$second_file_name' with $(( $(wc -l < $second_file_name) - 1 )) rows."
    else
        echo "Generated '$file_name' with $(( $(wc -l < $file_name) - 1 )) rows."
    fi
}


echo "Creating users..."

# Create members (who are also customers)
create_users $num_members Customer.csv Member.csv 
# Create customers
create_users $num_customers Customer.csv
# Create admins
create_users $num_admins Admin.csv
# Create guests
create_users $num_guests User.csv