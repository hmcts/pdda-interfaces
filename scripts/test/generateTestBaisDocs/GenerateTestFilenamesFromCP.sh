#!/bin/bash

# Directory to store the generated files
output_dir="generated_files"
mkdir -p "$output_dir"

# Variable to define the specific folder
specific_folder="/tmp/sftptestfolder/cp"
mkdir -p "$output_dir/$specific_folder"

# Paths to the example files
pd_example_file="CPPublicDisplayExample.xml"
wp_example_file="CPWebPageExample.xml"
dl_example_file="CPDailyListExample.xml"
fl_example_file="CPFirmListExample.xml"
wl_example_file="CPWarnedListExample.xml"

# Generate 100 files
for i in {1..1000}; do
  # Generate random 3 digit court code between 401 and 475
  court_code=$((RANDOM % 75 + 401))

  # Generate random datetime between now and 6 hours ago
  minutes_ago=$((RANDOM % 360))
  datetime=$(date -v -${minutes_ago}M +"%Y%m%d%H%M%S")

  # Randomly select one of the Cp doc types
  types=("PublicDisplay" "WebPage" "DailyList" "FirmList" "WarnedList")
  type=${types[$RANDOM % ${#types[@]}]}

  # Construct the filename
  filename="${type}_${court_code}_${datetime}.xml"

  # Create the file in the specific folder
  if [ "$type" == "PublicDisplay" ]; then
    cp "$pd_example_file" "$specific_folder/$filename"
  elif [ "$type" == "WebPage" ]; then
    cp "$wp_example_file" "$specific_folder/$filename"
  elif [ "$type" == "DailyList" ]; then
    cp "$dl_example_file" "$specific_folder/$filename"
  elif [ "$type" == "FirmList" ]; then
    cp "$fl_example_file" "$specific_folder/$filename"
  elif [ "$type" == "WarnedList" ]; then
    cp "$wl_example_file" "$specific_folder/$filename"
  else
    touch "$specific_folder/$filename"
  fi
done

echo "Generated files in the '$specific_folder' directory."
