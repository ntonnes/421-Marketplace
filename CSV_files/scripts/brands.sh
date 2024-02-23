#!/bin/bash


#######################
#    BrandPage.csv    #
#######################
num_brands=$1                                                   # Number of brand pages to create
echo -e "\nCreating $num_brands brand pages..."
echo "URL,Name,Description" > BrandPage.csv                     # Create BrandPage.csv and add headers

for ((i=1; i<=$num_brands; i++)); do                            # For each brand page to create
    url="www.421market.com/brand$i"                             # Create a URL
    name="Brand $i"                                             # Create a name                          
    
    if (( i % 5 == 0 )); then description="NULL"                # give ~20% NULL descriptions
    else description="This is the description for Brand $i."    # give ~80% a description
    fi

    echo "$url,$name,$description" >> BrandPage.csv             # Write the row to BrandPage.csv
done

echo "Generated 'BrandPage.csv' with $(( $(wc -l < BrandPage.csv) - 1 )) rows."


#####################
#    Manages.csv    #
#####################
max_manages=$2                                                          # Max number of admins per brand page
echo
echo "Assigning each brand page 1-$max_manages admins..."
echo "UserID,BrandPage,Since,ClearanceLevel" > Manages.csv              # Create Manages.csv and add headers

mapfile -t admins < <(tail -n +2 Admin.csv | cut -d, -f1)               # Get all admin IDs into array 'admins'
mapfile -t brand_pages < <(tail -n +2 BrandPage.csv | cut -d, -f1)      # Get all brand page URLs into array 'brand_pages'
readarray -t admins < <(printf '%s\n' "${admins[@]}" | shuf)            # Shuffle the admin IDs array
declare -A existingRelationships                                        # Declare an associative array to store existing relationships

j=0
next_admin () {                                                         # Function to get the next random admin ID
    if (( j < ${#admins[@]} - 1 )); then                                # If there are more admin IDs
        j=$((j + 1))                                                    # Next admin ID
    else 
        readarray -t admins < <(printf '%s\n' "${admins[@]}" | shuf)    # Shuffle the admin IDs array
        j=0                                                             # Reset counter
    fi
}

for ((i=0; i<$num_brands; i++)); do                                             # For each brand page

    num_manages=$((RANDOM % $max_manages+1))                                    # Random number of admins for this brand page       
    for ((k=0; k<$num_manages; k++)); do

        while [[ -n "${existingRelationships["${admins[j]},${brand_pages[i]}"]}" ]]; do
            next_admin
        done

        if (( k == 0 )); then clearance=5;                                      # The first admin always has clearance level 5                                                                       
        else clearance=$((RANDOM % 5 + 1)); fi                                  # The rest have a random clearance level                    

        since=$(date -d "$((RANDOM % 3653 + 1)) days ago" +'%Y-%m-%d')          # Generate a random date in the past 10 years     
        echo "${admins[j]},${brand_pages[i]},$since,$clearance" >> Manages.csv  # Write the row to Manages.csv      
        existingRelationships["${admins[j]},${brand_pages[i]}"]=1               # Add the new relationship to the array       
        next_admin                                                              # Get the next random admin ID
    done    
                                 
done

echo -e "Generated 'Manages.csv' with $(( $(wc -l < Manages.csv) - 1 )) rows.\n"