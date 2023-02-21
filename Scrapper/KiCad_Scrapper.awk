# Creator : Charles GRASSIN
# License : GPLv2

# This AWK script extracts the information in the 
# KiCad library format (.lib & .dcm files) and formats it into 
# a JSON file. To save a LOT of bytes, components are grouped
# by pinout. It is used by the ElectroDB Android App.

# Usage  : awk -f KiCad_Scrapper.awk link/to/kicad/libraries > db.json

# Sample output:
# {"components":[
# {
# 	"names":["Chip1","Chip2", ...],
# 	"descriptions":["Description for chip 1","Description for chip 2", ...],
# 	"datasheets":["Datasheet URL for chip 1","Datasheet URL for chip 2", ...],
# 	"category":"Category common to the chips",
# 	"package":"Package common to the chips",
# 	"pins":["Pin1","Pin2","Pin3",...]
# },
# ...
# ]}

BEGIN{
	IGNORECASE = 0;
	firstcomp = 0;
	ORS=""
	print "{\"components\":["
}

FNR==1 {
	# If file is a json file, add it to library
	if(FILENAME ~ /\.json$/){
		while (getline < FILENAME)
			print "\n"$0
		# print ","
		nextfile
	}

	# If file is not a .lib, skip it
	if(!(FILENAME ~ /\.lib$/)) nextfile

	# Extract category from filename
	category = FILENAME;
	sub(/^.*(\\|\/)/,"",category);
	sub(/\.lib$/,"",category);

	# Extract descriptions & datasheets from ".dcm" file (if exists, no effect otherwise)
	delete DCMdescriptions
	delete DCMdatasheets
	DCMPath=FILENAME;
	sub(/\.lib$/,".dcm",DCMPath);
	if(exists(DCMPath)){
		while (getline < DCMPath){
			if($0 ~ /^\$CMP /){
				DCMname = $2;
			}
			else if($0 ~ /^D /){
				sub(/^D /,"",$0);
				gsub(/"/,"",$0);
				DCMdescriptions[DCMname] = $0;
			}
			else if($0 ~ /^F / && $2  ~ /^http/){
				sub(/^F /,"",$0);
				DCMdatasheets[DCMname] = $0;
			}
		}
		close (DCMPath)
	}
}

# NAMES (and alternate names)
/^F1 /{
	# Reinit values
	nameNumber = 1;
	packageF2 = ""
	packageFP = ""
	delete pins
	delete names

	#Handle Component name
	sub(/^F1 "/, "", $0);
	sub(/".*/, "", $0);
	names[1]= $0;
}
/^ALIAS /{
	nameNumber += NF - 1;
	for (i = 2; i <= NF; i++) {
		names[i] = $i;
	}
	
}

# PACKAGE LIST
/^F2 /{
	packageF2 = formatPackage($2,1)
	if(packageF2 == "") packageF2 = $2
	sub("\"~?\"", "", packageF2)
}
/^\$FPLIST/{
	getline
	while($0 != "$ENDFPLIST"){
		packtmp = formatPackage($0,0)
		if(packageFP == "" || !(packtmp ~ packageFP))
			packageFP = packageFP packtmp
		getline
	}
}

# X : PinNames & number
/^X /{
	# Solution to sort issue (add padding zero's before pin number/name)
	# because "10" < "2" but "10" > "02". Padding 0's are
	# removed prior to printing the result
	while(length($3)<3)
		$3 = "0" $3

	#Pin output
	pins[$3] = $12 ":" $2;
	if($10 != "0" && $10 != "1")
		pins[$3] = pins[$3] "("$10")"
}

# ENDDEF : End of component definition, output it as JSON
/ENDDEF/{
	if(firstcomp) print(",");
	else firstcomp=1;

	#Names
	print "\n{\n\t\"names\":["
	for(i=1;i<=nameNumber;i++){
		if(i!=1) print(",");
		gsub(/\\/,"",names[i]);
		print "\""names[i]"\""
	}
	print "],"

	#Descriptions (for each name)
	print "\n\t\"descriptions\":["
	for(i=1;i<=nameNumber;i++){
		if(i!=1) print(",");
		print "\""DCMdescriptions[names[i]]"\""
	}
	print "],"

	#Datasheets (for each name)
	print "\n\t\"datasheets\":["
	for(i=1;i<=nameNumber;i++){
		if(i!=1) print(",");
		print "\""DCMdatasheets[names[i]]"\""
	}
	print "],"

	# Category
	print "\n\t\"category\":" "\"" category "\","

	# Package
	package = packageFP
	if(package=="" || !(packageF2 ~ packageFP))
		package = packageF2 package 
	sub(/\/$/,"",package)
	gsub("\"","",package);
	sub(/ $/,"",package);
	print "\n\t\"package\":" "\"" package "\"," 

	# Pins (in ascending order)
	print "\n\t\"pins\":["
	num = asorti(pins,indices);
	for(i=1;i <= num;i++){
		if(i!=1) print(",");
		pinName = pins[indices[i]];
		sub(/^0+/,"",indices[i]);
		print "{\""indices[i]"\":\""pinName"\"}"
	}
	print "]\n}"
}

END{
	print "\n]}"
	print nb
}

# Function to determine if a file exists using its filename
function exists(file    , line){
	if ( (getline line < file) > 0 ){
		close(file)
		return 1
	}
	else{
		return 0
	} 
}

# Returns true if arg is numeric, false otherwise
function isNumeric(stringToCheck){
	if (stringToCheck ~ /^[0-9]+$/)
		return 1
	else
		return 0
}

# Function to reformat the package name depending on the housing names
function formatPackage(packageString,isF2        ,output,packageNames,i,j){
	gsub(/\?/,"-",packageString);
	gsub(/\*/,"-",packageString);
	sub(/.*:/,"",packageString);
	
	# Generic
	# packageNames[i++]="Arduino";
	# packageNames[i++]="Nucleo";
	# packageNames[i++]="Raspberry Pi";
	packageNames[i++]="Valve"; # Valve
	packageNames[i++]="Display"; # Display
	packageNames[i++]="Oscillator"; # Oscillator

	# CSP
	packageNames[i++]="WLCSP"; # QFN
	packageNames[i++]="LFCSP"; # QFN
	packageNames[i++]="PLCC"; # QFP
	packageNames[i++]="MLPQ"; # QFN
	packageNames[i++]="LLP"; # QFN
	packageNames[i++]="LLC"; # QFN
	packageNames[i++]="DIP"; # DIP
	packageNames[i++]="SIP"; # DIP
	packageNames[i++]="QFP"; # QFP
	packageNames[i++]="UQFN"; # QFN
	packageNames[i++]="DFN"; # QFN
	packageNames[i++]="QFN"; # QFN
	packageNames[i++]="SON"; # QFN
	packageNames[i++]="MLF"; # QFN

	# SOT/TO/SC
	packageNames[i++]="SOT-[0-9]+"; # SOT 23
	packageNames[i++]="TO-[0-9]+";
	packageNames[i++]="SC-[0-9]+";
	
	# Array
	packageNames[i++]="LGA"; # BGA
	packageNames[i++]="BGA"; # BGA
	packageNames[i++]="PGA"; # BGA

	# Diodes
	packageNames[i++]="MELF"; # SMD
	packageNames[i++]="SMA"; # SMD
	packageNames[i++]="SMB"; # SMD
	packageNames[i++]="SMC"; # SMD
	packageNames[i++]="SOD"; # SMD

	# Small Outline
	packageNames[i++]="TSSOP"; # SO
	packageNames[i++]="HTSOP"; # SO
	packageNames[i++]="VSSOP"; # SO
	packageNames[i++]="TSOP"; # SO
	packageNames[i++]="SSOP"; # SO
	packageNames[i++]="MSOP"; # SO
	packageNames[i++]="SOIC"; # SO
	packageNames[i++]="SOP"; # SO
	packageNames[i++]="SO"; # SO

	
	#packageNames[i++]="SMD";

	for(j=0;j<i;j++){
		if(isF2)
			packageNames[j] = packageNames[j] "(-[0-9]+)?"
		if(packageString ~ packageNames[j]){
			return gensub(".*(" packageNames[j] ").*","\\1","g",packageString) "/"
		}
	}
	
	return "";
}
