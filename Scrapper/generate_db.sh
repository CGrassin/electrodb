#!/bin/sh

rm ../ElectroDB/app/src/main/res/raw/db.json &&
awk -f KiCad_Scrapper.awk Input/* manual_parts.json > ../ElectroDB/app/src/main/res/raw/db.json