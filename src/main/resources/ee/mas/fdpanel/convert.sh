#!/bin/bash
clear
echo
echo "----------------------------------------"
echo -e "\033[34mVäljaande teisendaja\033[0m"
echo "----------------------------------------"
echo
echo "Mälupulkade otsimine..."
declare -a mounts
for i in $(lsblk -o MOUNTPOINTS --raw -n); do
    [ -e "$i/E_INFO/edition.txt" ] && mounts+=("$i")
done
sleep 1
device=""
for mount in "${mounts[@]}"; do
    echo -n "Kas soovite kasutada seadet $mount? [Y/N]"
    read -rsn1 val
    echo
    [ "$val" = "Y" ] && device="$mount" && break
    [ "$val" = "y" ] && device="$mount" && break
done
[ "$device" = "" ] && echo "Kasutaja ei valinud sobivat seadet!" && exit
echo "Jätkame seadme $device kasutamist..."
edition="$(cat $device/E_INFO/edition.txt)"
[ "$edition" = "Basic" ] && echo -e "\033[92mBasic\033[0m väljaande teisendamine pole toetatud." && echo -ne "Vajutage ükskõik millist klahvi, et väljuda . . . " && read -rsn1 dummy && echo && exit
[ "$edition" = "Ultimate" ] && to="Premium" && editionO="\033[95mUltimate\033[0m" && toO="\033[91mPremium\033[0m"
[ "$edition" = "Premium" ] && to="Ultimate" && editionO="\033[91mPremium\033[0m" && toO="\033[95mUltimate\033[0m"
[ "$to" = "" ] && echo "Sobimatu väljaanne. Selle mälupulga teisendamine pole toetatud." && echo -ne "Vajutage ükskõik millist klahvi, et väljuda . . . " && read -rsn1 dummy && echo && exit
clear

echo
echo "----------------------------------------"
echo -e "\033[34mVäljaande teisendaja\033[0m"
echo "----------------------------------------"
echo
echo -e "Väljaanne $editionO tuvastatud. Kas soovite"
echo -e "selle teisendada väljaandeks $toO? [Y/N]"
echo
[ "$edition" = "Premium" ] && echo -e "\033[92mSee tegevus ei mõjuta olemasolevaid faile\033[0m"
[ "$edition" = "Ultimate" ] && echo -e "\033[101;5m\033[93;5mHoiatus: Teisendamise käigus KUSTUTATAKSE" && echo -e "kõik failid \"markuse asjad\" kaustas!     \033[0m"
echo
read -rsn1 val
continue="false"
[ "$val" = "Y" ] && continue="true"
[ "$val" = "y" ] && continue="true"
[ "$continue" = "false" ] && echo "Katkestasite toimingu." && echo -ne "Vajutage ükskõik millist klahvi, et väljuda . . . " && read -rsn1 dummy && echo && exit
clear
echo
echo "----------------------------------------"
echo -e "\033[34mVäljaande teisendaja\033[0m"
echo "----------------------------------------"
echo "Teisendamine algab 10 sekundi pärast"
echo -e "\033[93;5mHoiatus: Ärge teisendamise käigus eemaldage mälupulka! \033[0m"
echo
echo "Teisendamise eesmärk:"
echo -e "$editionO > $toO"
echo
sleep 5
echo "Okei, alustame siis..."
[ "$to" = "Ultimate" ] && echo "Vajalike kaustade loomine..." && mkdir "$mount/markuse asjad" && mkdir "$mount/markuse asjad/markuse asjad"
[ "$to" = "Ultimate" ] && echo "Eksisteeriva väljaande info kustutamine..." && rm "$mount/E_INFO/edition.txt" && rm "$mount/E_INFO/edition.cmd"
[ "$to" = "Ultimate" ] && echo "Uue väljaande info kirjutamine..."
[ "$to" = "Ultimate" ] && echo "SET verifycode=$RANDOM$RANDOM" > "$mount/E_INFO/edition.cmd"
[ "$to" = "Ultimate" ] && echo "SET edition=Ultimate" >> "$mount/E_INFO/edition.cmd"
[ "$to" = "Ultimate" ] && echo "SET verifyspace=" >> "$mount/E_INFO/edition.cmd"
[ "$to" = "Ultimate" ] && echo -ne "Ultimate" > "$mount/E_INFO/edition.txt"
[ "$to" = "Ultimate" ] && echo -e "\033[92mÕnnestus\033[0m! Väljaanne teisendati edukalt!" && echo -ne "Vajutage ükskõik millist klahvi, et väljuda . . . " && read -rsn1 dummy && echo && exit
# Hirmus
echo "Markuse asjade kustutamine..."
[ "$mount" = "" ] && echo -e "\033[91;5mJuurkatalogi '/' kustutamise kaitse aktiveeriti: midagi läks väga valesti!!!\033[0m" && exit
rm -rf "$mount/markuse asjad"
echo "Väljaande info kustutamine..."
rm "$mount/E_INFO/edition.txt"
rm "$mount/E_INFO/edition.cmd"
echo "Uue väljaande info kirjutamine..."
echo "SET verifycode=$RANDOM$RANDOM" > "$mount/E_INFO/edition.cmd"
echo "SET edition=Premium" >> "$mount/E_INFO/edition.cmd"
echo "SET verifyspace=" >> "$mount/E_INFO/edition.cmd"
echo -ne "Premium" > "$mount/E_INFO/edition.txt"
echo -e "\033[92mÕnnestus\033[0m! Väljaanne teisendati edukalt!"
echo -ne "Vajutage ükskõik millist klahvi, et väljuda . . . "
read -rsn1 dummy && echo && exit
