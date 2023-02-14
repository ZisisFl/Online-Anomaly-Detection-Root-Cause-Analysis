# Overview
Creating a Docker image for data generation using [TPC-DS tool](https://www.tpc.org/tpcds/).

# Instructions
In order to create a Docker image of the TPC-DS tool you first need to download it by filling the required form in the [website](https://www.tpc.org/tpcds/).

After you download the TPC-DS zip file you need to:
- Unzip the downloaded file
- Put the unziped folder under in ./dsdgen directory
- Rename the folder to DSGen-software-code

By doing so the DSGen-software-code folder will be grayed out in your IDE as it is part of the of the ignored directories in the .gitignore file.

# Extras
- TPC-DS in PostgreSQL [link](https://ankane.org/tpc-ds)