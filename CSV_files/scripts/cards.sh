# Get the number of null cards and max number of cards per customer from the script arguments
num_null_cards=$1
max_cards_per_customer=$2

# Clear the file and add headers
echo "CardNum,CardExp,UserID" > Card.csv

# Get all user IDs
mapfile -t userIDs < <(tail -n +2 Customer.csv | cut -d, -f1)

# Declare an associative array to store card numbers
declare -A cardNumbers

# Initialize counter for processed cards
processed_cards=0

# Calculate total number of cards
total_cards=$((num_null_cards + ${#userIDs[@]} * max_cards_per_customer))

# For each user ID
for userID in "${userIDs[@]}"
do
    # Generate a random number of cards for this user ID
    num_cards=$((RANDOM % (max_cards_per_customer+1)))

    # For each card to create
    for ((i=1; i<=$num_cards; i++))
    do
        # Generate a unique card number in the range 1000000000000000-9999999999999999
        while true; do
            cardNum=$((RANDOM % 8999999999999999 + 1000000000000000))
            if [ -z "${cardNumbers[$cardNum]}" ]; then
                cardNumbers[$cardNum]=1
                break
            fi
        done

        # Generate a random expiration date between now and 5 years in the future
        cardExp=$(date -d "$((RANDOM % 1825)) days" +%Y-%m-%d)

        # Write the row to Card.csv
        echo "$cardNum,$cardExp,$userID" >> Card.csv

        # Increment counter for processed cards
        processed_cards=$((processed_cards + 1))

        # Calculate percentage of progress as a floating-point number and convert it to an integer
        percent=$(awk "BEGIN {printf \"%.0f\", 100 * $processed_cards / $total_cards}")

        # Print percentage of progress
        printf "\rProgress: %d%%" $percent
    done
done

# For each null card to create
for ((i=1; i<=$num_null_cards; i++))
do
    # Generate a unique card number in the range 1000000000000000-9999999999999999
    while true; do
        cardNum=$((RANDOM % 8999999999999999 + 1000000000000000))
        if [ -z "${cardNumbers[$cardNum]}" ]; then
            cardNumbers[$cardNum]=1
            break
        fi
    done

    # Generate a random expiration date between now and 5 years in the future
    cardExp=$(date -d "$((RANDOM % 1825)) days" +%Y-%m-%d)

    # Write the row to Card.csv
    echo "$cardNum,$cardExp,NULL" >> Card.csv

    # Increment counter for processed cards
    processed_cards=$((processed_cards + 1))

    # Calculate percentage of progress as a floating-point number and convert it to an integer
    percent=$(awk "BEGIN {printf \"%.0f\", 100 * $processed_cards / $total_cards}")

    # Print percentage of progress
    printf "\rProgress: %d%%" $percent
done

# Print success message
echo -e "\nCard generation completed successfully."