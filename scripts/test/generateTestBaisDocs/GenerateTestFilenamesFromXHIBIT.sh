#!/bin/bash

# Directory to store the generated files
output_dir="generated_files"
mkdir -p "$output_dir"

# Variable to define the specific folder
specific_folder="/tmp/sftptestfolder/xhibit"
mkdir -p "$output_dir/$specific_folder"

# Paths to the example files
xpd_example_file="XHIBITPublicDisplayExample.xml"
cpd_example_file="CPPublicDisplayExample.xml"
xdl_example_file="XHIBITDailyListExample.xml"

# Generate 100 files
for i in {1..1000}; do
  # Generate random 4 digit number
  four_digit_number=$(printf "%04d" $((RANDOM % 10000)))

  # Generate random 2 digit number
  two_digit_number=$(printf "%02d" $((RANDOM % 100)))

  # Generate random 3 digit court code between 401 and 475
  court_code=$((RANDOM % 75 + 401))

  # Generate random datetime between now and 6 hours ago
  minutes_ago=$((RANDOM % 360))
  datetime=$(date -v -${minutes_ago}M +"%Y%m%d%H%M%S")

  # Randomly select one of XPD, XDL, or CPD
  types=("XPD" "XDL" "CPD")
  type=${types[$RANDOM % ${#types[@]}]}

  # Construct the filename
  filename="PDDA_${type}_${four_digit_number}_${two_digit_number}_${court_code}_${datetime}"

  # Create the file in the specific folder
  if [ "$type" == "XPD" ]; then
    cp "$xpd_example_file" "$specific_folder/$filename"
  elif [ "$type" == "CPD" ]; then
    cp "$cpd_example_file" "$specific_folder/$filename"
  elif [ "$type" == "XDL" ]; then
    cp "$xdl_example_file" "$specific_folder/$filename"
  else
    touch "$specific_folder/$filename"
  fi
done

echo "Generated files in the '$specific_folder' directory."
