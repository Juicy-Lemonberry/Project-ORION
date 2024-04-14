#!/bin/bash

# Backup the EXTERNALLY-MANAGED file
sudo mv /usr/lib/python3.11/EXTERNALLY-MANAGED /usr/lib/python3.11/EXTERNALLY-MANAGED.bak

# Install the dependencies required
pip install -r requirements.txt

# Restore the EXTERNALLY-MANAGED file
sudo mv /usr/lib/python3.11/EXTERNALLY-MANAGED.bak /usr/lib/python3.11/EXTERNALLY-MANAGED
