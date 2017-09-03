#!/bin/bash

function readyEnvironment()
{
    echo "Ready environment~"
    sudo apt-get update && sudo apt-get install git-core gnupg flex bc bison gperf libsdl1.2-dev libesd0-dev libwxgtk3.0-dev squashfs-tools build-essential zip curl libncurses5-dev zlib1g-dev openjdk-8-jre openjdk-8-jdk pngcrush schedtool libxml2 libxml2-utils xsltproc lzop libc6-dev schedtool g++-multilib lib32z1-dev lib32ncurses5-dev gcc-multilib maven tmux screen w3m ncftp liblz4-tool python nginx unzip -y
    git config --global user.email $1
    git config --global user.name $2
    wget https://www.imagemagick.org/download/ImageMagick.tar.gz
    tar -zxvf ImageMagick.tar.gz
    cd ImageMagick-7*
    ./configure
    make
    make install
    ldconfig /usr/local/lib
    rm -rf ~/ImageMagick*
}

function getSource()
{
    case $1 in
        '1')
            doLineageOS
        ;;
        '2')
            doRR
        ;;
        *)
            echo "Error input!"
        ;;
    esac
}

function doLineageOS()
{
    echo "This is script for LineageOS!"
    mkdir ~/LineageOS && cd ~/LineageOS
    echo "Please enter which branch?"
    read branch
    repo init -u git://github.com/LineageOS/android.git -b $branch
    repo sync -f --force-sync --no-clone-bundle
    source ./build/envsetup.sh
    setCache
}

function doRR()
{
    echo "This is script for RR!"
    mkdir ~/RR && cd ~/RR
    echo "Please enter which branch?"
    read branch
    repo init -u https://github.com/ResurrectionRemix/platform_manifest.git -b $branch
    repo sync -f --force-sync --no-clone-bundle
    source ./build/envsetup.sh
    setCache
}

function setCache()
{
    export USE_CCACHE=1
    ./prebuilts/misc/linux-x86/ccache/ccache -M 50G
    export CCACHE_COMPRESS=1
    export JACK_SERVER_VM_ARGUMENTS="-Dfile.encoding=UTF-8 -XX:+TieredCompilation -Xmx8G"
}

function importVendor()
{
    read codeName
    . build/envsetup.sh
    echo "Start sync device vendor..."
    breakfast $codeName
}

function copyFonts()
{
    case $romType in
        '1') romName="LineageOS";;
        '2') romName="RR";;
    esac
    echo "Copying fonts..."
    cp fonts/NotoSansCJK-Regular.ttc ~/$romName/external/noto-fonts/cjk/NotoSansCJK-Regular.ttc
    cp fonts/Roboto-Regular.ttf ~/$romName/external/roboto-fonts/Roboto-Regular.ttf
    cp fonts/RobotoCondensed-Regular.ttf ~/$romName/external/roboto-fonts/RobotoCondensed-Regular.ttf
    cp fonts/DroidSansMono.ttf ~/$romName/frameworks/base/data/fonts/DroidSansMono.ttf
}

function copyNetworkTraffic()
{
    case $romType in
        '1') romName="LineageOS";;
        '2') romName="RR";;
    esac
    echo "Copying NetworkTraffic..."
    cp netspeed/Mystery0Traffic.java ~/$romName/frameworks/base/packages/SystemUI/src/com/android/systemui/statusbar/policy/
}

function copySystemUI()
{
    case $romType in
        '1') romName="LineageOS";;
        '2') romName="RR";;
    esac
    echo "Copying SystemUI..."
    # copy nav icon
    cp SystemUI/drawable-xxxhdpi/* ~/$romName/frameworks/base/packages/SystemUI/res/drawable-xxxhdpi/
    
    if [ $romType -eq '2' ]
    then
        cp SystemUI/drawable/stat_sys_vpn_ic.xml ~/$romName/frameworks/base/packages/SystemUI/res/drawable/
    else
        cp SystemUI/drawable/* ~/$romName/frameworks/base/packages/SystemUI/res/drawable/
    fi
}

function copyOpalayout()
{
    case $romType in
        '1') romName="LineageOS";;
        '2') romName="RR";;
    esac
    echo "Copying OpaLayout..."
    mkdir ~/$romName/frameworks/base/packages/SystemUI/src/com/google
    mkdir ~/$romName/frameworks/base/packages/SystemUI/src/com/google/android
    mkdir ~/$romName/frameworks/base/packages/SystemUI/src/com/google/android/systemui
    cp SystemUI/src/OpaLayout.java ~/$romName/frameworks/base/packages/SystemUI/src/com/google/android/systemui/OpaLayout.java
    cp SystemUI/layout/home.xml ~/$romName/frameworks/base/packages/SystemUI/res/layout/home.xml
}

function editSourceCode()
{
    case $romType in
        '1')
            vim ~/LineageOS/.repo/local_manifests/roomservice.xml
            vim ~/LineageOS/frameworks/base/core/res/res/values/themes.xml
            vim ~/LineageOS/frameworks/base/core/res/res/values/themes_material.xml
            vim ~/LineageOS/frameworks/base/core/res/res/values/config.xml
            vim ~/LineageOS/frameworks/base/packages/SystemUI/res/layout/status_bar.xml
            vim ~/LineageOS/frameworks/base/packages/SystemUI/res/values/dimens.xml
            vim ~/LineageOS/frameworks/base/packages/SystemUI/res/values/ids.xml
            vim ~/LineageOS/frameworks/base/packages/SystemUI/res/values/styles.xml
        ;;
        '2')
            vim ~/LineageOS/.repo/local_manifests/roomservice.xml
        ;;
    esac
}

function showMenu()
{
    cd ~/Ubuntu-Command/
    # make sure this is in Ubuntu-Command dir
    echo " "
    echo "*************  Welcome  *************"
    echo "*************************************"
    echo "** 1. Build the environment        **"
    echo "** 2. Sync source code             **"
    echo "** 3. Change the source code       **"
    echo "** 4. Compile source code          **"
    echo "** 5. Put package into web root    **"
    echo "** 0. Exit                         **"
    echo "*************************************"
    echo "*************************************"
    echo " "
    echo -n "Please enter your operation: "
    read operation
    case $operation in
        '0') #Build the environment
            echo "Good Bye!"
        ;;
        '1')
            if [ $gitEmail = "null" ]
            then
                read -p "Please enter your email for github: " temp
                gitEmail=$temp
            fi
            if [ $gitUsername = "null" ]
            then
                read -p "Please enter your username for github: " temp
                gitUsername=$temp
            fi
            echo $gitEmail
            echo $gitUsername
            readyEnvironment $gitEmail $gitUsername
            showMenu
        ;;
        '2') #Sync source code
            if [ $romType -ne "0" ]
            then
                case $romType in
                    '1') romName="LineageOS";;
                    '2') romName="RR";;
                esac
                echo "You choosed $romName, do you want to continue?[Y/n]"
                read temp
                echo $temp
                if [ $temp="n" ]
                then
                    romType=0
                fi
            fi
            if [ $romType -eq "0" ]
            then
                echo "1.LineageOS"
                echo "2.RR"
                echo -n "Please choose which rom: "
                read choose
            fi
            romType=$choose
            getSource $choose
            importVendor
            showMenu
        ;;
        '3') #Change the source code
            if [ $romType -ne "0" ]
            then
                case $romType in
                    '1') romName="LineageOS";;
                    '2') romName="RR";;
                esac
                echo "You choosed $romName, do you want to continue?[Y/n]"
                read temp
                echo $temp
                if [ $temp="n" ]
                then
                    romType=0
                fi
            fi
            if [ $romType -eq "0" ]
            then
                echo "1.LineageOS"
                echo "2.RR"
                echo -n "Please choose which rom: "
                read choose
            fi
            romType=$choose
            case $romType in
                '1')
                    copyFonts
                    copyNetworkTraffic
                    copySystemUI
                    copyOpalayout
                    echo "Copying done!"
                ;;
                '2')
                    copyFonts
                    copyNetworkTraffic
                    copySystemUI
                    echo "Copying done!"
                ;;
            esac
            editSourceCode
            showMenu
    esac
}