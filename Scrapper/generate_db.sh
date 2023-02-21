#!/bin/sh

rm ../ElectroDB/app/src/main/res/raw/db.json &&
awk -f KiCad_Scrapper.awk Input/kicad-symbols/* Input/digikey-symbols/* manual_parts.json > ../ElectroDB/app/src/main/res/raw/db.json