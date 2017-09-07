#!/bin/bash
echo "Do you want to edit traffic layout of statusbar?[y/N]"
read temp
if [ -r $temp ]; then
	return
fi
if [ $temp = 'y' ] || [ $temp = 'Y' ]; then
    # vim ~/RR/frameworks/base/packages/SystemUI/res/layout/status_bar.xml
    echo "test"
fi