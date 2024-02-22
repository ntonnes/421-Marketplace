#!/bin/bash
./scripts/clear.sh

echo
while true; do
    read -p "Would you like to generate a small, medium, or large data set (s/m/l)? " size
    if [[ "$size" == "s" ]]; then
        x=1; break
    elif [[ "$size" == "m" ]]; then
        x=2; break
    elif [[ "$size" == "l" ]]; then
        x=5; break
    else
        echo "Invalid input. Please enter 's', 'm', or 'l'."
    fi
done


# users.sh num_cust num_mem num_admin num_guest
#   num_cust + num_mem  --> number of rows in Customer.csv
#   num_mem             --> number of rows in Member.csv
#   num_admin           --> number of rows in Admin.csv
#   sum of all four     --> number of rows in User.csv
n=$(( x*5 ))
./scripts/users.sh $n $n $n $n


# categories.sh num_cat
#   num_categories --> number of rows in Category.csv
n=$(( x*5 ))
./scripts/categories.sh $n


# brands.sh num_brand max_manages
#   num_brands  --> number of rows in BrandPage.csv
#   max_manages --> maximum number of admins per brand page
#   20% of the brands will have no description 
n=$(( x*5 ))
m=$(( x*2 ))
if (( m > 6 )); then m=6; fi
./scripts/brands.sh $n $m


echo
# models.sh num_models max_cat
#   num_models --> number of rows in Model.csv
#   max_cat    --> each model is assigned 0-max_cat categories
n=$(( x*6 ))
m=$(( x+1 ))
./scripts/models.sh $n $m

echo
# coupons.sh max_coup
#   max_coup --> each member is assigned 0-max_coup coupons
n=$(( x*2 ))
./scripts/coupons.sh $n


echo
echo "Creating carts..."
# carts.sh max_items max_copies
#   each user is assigned 1-max_items copies of 0-max_copies models
n=$(( x ))
m=$(( x+1 ))
./scripts/carts.sh $n $m
echo "Generated 'InCart.csv' with $(( $(wc -l < InCart.csv) - 1 )) rows."

echo
echo "Creating restocks..."
# restocks.sh num_restock
#   num_restock is the number of rows in Restock.csv
n=$(( x*6 ))
m=$(( x*4 ))
./scripts/restocks.sh $n $m
echo "Generated 'Restock.csv' with $(( $(wc -l < Restock.csv) - 1 )) rows."

echo
echo "Creating cards..."
# cards.sh num_cards max_cards
#   num_cards is the number of rows in Card.csv
#   each customer is assigned 0-max_cards cards
#   remaining cards are assigned to guests (UserID is NULL)
n=$(( x*6 ))
m=$(( x*2 ))
./scripts/cards.sh $n $m
echo "Generated 'Card.csv' with $(( $(wc -l < Card.csv) - 1 )) rows."

echo
echo "Creating products..."
# products.sh max_products total_orders ordered_products
#   max_products_per_model is the maximum number of existing products per model
#   total_orders is the maximum number of orders that can be placed
#   each product has a 33% chance of being assigned to an order randomly
n=$(( x*5 ))
m=$(( x*10 ))
./scripts/products.sh $n $m
echo "Generated 'Product.csv' with $(( $(wc -l < Product.csv) - 1 )) rows."

echo
echo "Creating orders..."
# orders.sh
./scripts/orders.sh
echo "Generated 'Order.csv' with $(( $(wc -l < Order.csv) - 1 )) rows."

echo
echo "Creating shipments.."
# shipments.sh num_shippers max_size
#   num_shippers is the number of shippers to iterate through
#   each shipment has 1-max_size products
n=$(( x ))
m=$(( x*2 ))
./scripts/shipments.sh $n $m
echo "Generated 'Shipment.csv' with $(( $(wc -l < Shipment.csv) - 1 )) rows."

echo "Creating reviews.."
./scripts/reviews.sh
echo "Generated 'Review.csv' with $(( $(wc -l < Review.csv) - 1 )) rows."

echo
echo "Data refreshed successfully."