#!/bin/bash

# Wipe the database then create tables and load data into them
./droptbl.sh
./createtbl.sh
./loaddata.sh
