mkdir ~/LineageOS && cd ~/LineageOS
repo init -u git://github.com/LineageOS/android.git -b cm-14.1
repo sync -f --force-sync --no-clone-bundle
source ~/LineageOS/build/envsetup.sh
export USE_CCACHE=1
~/LineageOS/prebuilts/misc/linux-x86/ccache/ccache -M 50G
export CCACHE_COMPRESS=1
export JACK_SERVER_VM_ARGUMENTS="-Dfile.encoding=UTF-8 -XX:+TieredCompilation -Xmx8G"
source build/envsetup.sh