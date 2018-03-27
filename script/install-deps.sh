#!/bin/bash

# Verilator
sudo apt-get install git make autoconf g++ flex bison
git clone http://git.veripool.org/git/verilator   # Only first time
unsetenv VERILATOR_ROOT  # For csh; ignore error if on bash
unset VERILATOR_ROOT  # For bash
cd verilator
git pull        # Make sure we're up-to-date
git tag v3.872  # See what versions exist
autoconf        # Create ./configure script
./configure
make
sudo make install

# SpinalHDL
git clone https://github.com/SpinalHDL/SpinalHDL.git
cd SpinalHDL
sbt clean compile publish-local
