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
	IGNORECASE = 1;
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
	package = ""
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
	package = $2 " "
}
/^\$FPLIST/{
	getline
	while($0 != "$ENDFPLIST"){
		package = package $1 " "
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
	package = formatPackage(package);
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
function formatPackage(packageString        ,output,packageNames,i,j){
	output = ""
	i=0

	if (packageString == "") return "";
	
	# Generic
	packageNames[i++]="Arduino";
	packageNames[i++]="Nucleo";
	packageNames[i++]="Raspberry Pi";
	packageNames[i++]="VALVE";
	packageNames[i++]="SMD";

	packageNames[i++]="LLP";
	packageNames[i++]="LLC";
	packageNames[i++]="MELF";
	packageNames[i++]="DIP";
	packageNames[i++]="QFN";
	packageNames[i++]="QFP";
	packageNames[i++]="MLF";

	# SOT
	packageNames[i++]="SOT23";
	packageNames[i++]="SOT323";
	packageNames[i++]="SOT416";
	packageNames[i++]="SOT353";
	packageNames[i++]="SOT363";
	packageNames[i++]="SOT143";
	packageNames[i++]="SOT343";
	packageNames[i++]="SOT490";
	packageNames[i++]="SOT89";
	packageNames[i++]="SOT223";
	packageNames[i++]="SOT";

	
	# Small Outline
	packageNames[i++]="SSOP";
	packageNames[i++]="SOIC";
	packageNames[i++]="SOP";
	packageNames[i++]="SOD";
	packageNames[i++]="SO";
	
	# Array
	packageNames[i++]="LGA";
	packageNames[i++]="BGA";
	packageNames[i++]="PGA";

	#TO
	packageNames[i++]="TO-3";
	packageNames[i++]="TO-5";
	packageNames[i++]="TO-18";
	packageNames[i++]="TO-39";
	packageNames[i++]="TO-46";
	packageNames[i++]="TO-66";
	packageNames[i++]="TO-92";
	packageNames[i++]="TO-99";
	packageNames[i++]="TO-100";
	packageNames[i++]="TO-126";
	packageNames[i++]="TO-220";
	packageNames[i++]="TO-226";
	packageNames[i++]="TO-247";
	packageNames[i++]="TO-251";
	packageNames[i++]="TO-252";
	packageNames[i++]="TO-262";
	packageNames[i++]="TO-263";
	packageNames[i++]="TO-274";
	packageNames[i++]="TO";

	# Special cases: MCU modules
	if(packageString ~ "STLink") packageString = "Nucleo"
	if(packageString ~ "*SODIMM*") packageString = "Raspberry Pi"
	if(packageString ~ "WEMOS" || 
		packageString ~ "BeagleBoard" || 
		packageString ~ "Maple_Mini" || 
		packageString ~ "Onion_Omega" ||
		packageString ~ "NEXTTHINGCO") packageString = "Arduino"

	for(j=0;j<i;j++){
		if(packageString ~ packageNames[j]){
			output=output packageNames[j] "/"
			gsub(packageNames[j],"",packageString)
		}
	}
	sub(/\/$/,"",output)

	if (output == "") {
		gsub("\"","",packageString);
		sub(/ $/,"",packageString);
		return packageString;
	}
	else
		return output
}
