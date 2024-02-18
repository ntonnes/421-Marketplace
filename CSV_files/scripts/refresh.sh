#!/bin/bash
./scripts/clear.sh


echo
# users.sh num_customers num_members num_admins num_guests
#   num_customers + num_members --> number of rows in Customer.csv
#   num_members                 --> number of rows in Member.csv
#   num_admins                  --> number of rows in Admin.csv
#   sum of all fout             --> number of rows in User.csv
./scripts/users.sh 25 25 25 25


echo
# categories.sh num_categories
#   num_categories --> number of rows in Category.csv
./scripts/categories.sh 20


echo
# brands.sh num_brands max_manages
#   num_brands  --> number of rows in BrandPage.csv
#   max_manages --> maximum number of admins per brand page
#   20% of the brands will have no description 
./scripts/brands.sh 20 5


echo
# models.sh num_models max_cat_per_model
#   num_models --> number of rows in Model.csv
#   each model is assigned 0-max_cat_per_model categories
./scripts/models.sh 30 3

echo
echo "Creating coupons..."
# coupons.sh max_coupons_per_member
#   each member is assigned 0-max_coupons_per_member coupons
./scripts/coupons.sh 5
echo "Generated 'Coupon.csv' with $(( $(wc -l < Coupon.csv) - 1 )) rows."

echo
echo "Creating carts..."
# carts.sh max_items_per_cart max_copies_per_item
#   each user is assigned 1-max_items_per_cart copies of 0-max_copies_per_item models
./scripts/carts.sh 5 3
echo "Generated 'InCart.csv' with $(( $(wc -l < InCart.csv) - 1 )) rows."

echo
echo "Creating restocks..."
# restocks.sh num_restocks
#   num_restocks is the number of rows in Restock.csv
./scripts/restocks.sh 30
echo "Generated 'Restock.csv' with $(( $(wc -l < Restock.csv) - 1 )) rows."

echo
echo "Creating cards..."
# cards.sh num_cards max_cards_per_user
#   num_cards is the number of rows in Card.csv
#   each customer is assigned 0-max_cards_per_user cards
#   remaining cards are assigned to guests (UserID is NULL)
./scripts/cards.sh 30 4
echo "Generated 'Card.csv' with $(( $(wc -l < Card.csv) - 1 )) rows."

echo
echo "Creating products..."
# products.sh max_products_per_model total_orders ordered_products_limit
#   max_products_per_model is the maximum number of existing products per model
#   total_orders is the maximum number of orders that can be placed
#   each product has a 33% chance of being assigned to an order randomly
./scripts/products.sh 20 50
echo "Generated 'Product.csv' with $(( $(wc -l < Product.csv) - 1 )) rows."

echo
echo "Creating orders..."
# orders.sh
./scripts/orders.sh
echo "Generated 'Order.csv' with $(( $(wc -l < Order.csv) - 1 )) rows."

echo
echo "Creating shipments.."
# shipments.sh num_shippers max_products
#   num_shippers is the number of shippers to iterate through
#   each shipment has 1-max_products products
./scripts/shipments.sh 3 5
echo "Generated 'Shipment.csv' with $(( $(wc -l < Shipment.csv) - 1 )) rows."

echo
echo "Data refreshed successfully."