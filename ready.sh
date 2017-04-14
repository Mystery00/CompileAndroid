sudo apt-get update && sudo apt-get install git-core gnupg flex bc bison gperf libsdl1.2-dev libesd0-dev libwxgtk3.0-dev squashfs-tools build-essential zip curl libncurses5-dev zlib1g-dev openjdk-8-jre openjdk-8-jdk pngcrush schedtool libxml2 libxml2-utils xsltproc lzop libc6-dev schedtool g++-multilib lib32z1-dev lib32ncurses5-dev gcc-multilib maven tmux screen w3m ncftp liblz4-tool python nginx -y
wget https://www.imagemagick.org/download/ImageMagick.tar.gz
tar xvzf ImageMagick.tar.gz
cd ImageMagick*
./configure
make
make install
ldconfig /usr/local/lib
rm -rf ~/ImageMagick*
rm ~/ImageMagick*
mkdir ~/bin
PATH=~/bin:$PATH
curl http://commondatastorage.googleapis.com/git-repo-downloads/repo > ~/bin/repo
chmod a+x ~/bin/repo
mkdir ~/LineageOS && cd ~/LineageOS
git config --global user.email "mystery0dyl520@gmail.com"
git config --global user.name "Mystery0"
repo init -u git://github.com/LineageOS/android.git -b cm-14.1
repo sync -f --force-sync --no-clone-bundle
~/LineageOS/build/envsetup.sh
export USE_CCACHE=1
~/LineageOS/prebuilts/misc/linux-x86/ccache/ccache -M 50G
export CCACHE_COMPRESS=1
export JACK_SERVER_VM_ARGUMENTS="-Dfile.encoding=UTF-8 -XX:+TieredCompilation -Xmx8G"
source build/envsetup.sh