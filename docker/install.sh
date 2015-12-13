#!/bin/bash

set -e

pacman -Sy --noconfirm
pacman -S --noconfirm archlinux-keyring
pacman -S --noconfirm xorg-server-xvfb sdl glu
yes | pacman -S gtk2

unlink /usr/lib/libjpeg.so || true
cp /tmp/files/libjpeg.so.62.1.0 /usr/lib
ln -s /usr/lib/libjpeg.so.62.1.0 /usr/lib/libjpeg.so.62
ln -s /usr/lib/libjpeg.so.62 /usr/lib/libjpeg.so

mkdir /usr/local/games/zandronum
cd /usr/local/games/zandronum

tar xvjf /tmp/files/zandronum.tar.bz2

cd /opt
tar zxvf /tmp/files/jre.tar.gz
ln -s jdk* java

if ! grep -q 'JAVA_HOME' /etc/profile; then
  echo 'export JAVA_HOME=/opt/java' >> /etc/profile
  echo 'export PATH=$PATH:$JAVA_HOME/bin' >> /etc/profile
fi

rm -rf /tmp/*
