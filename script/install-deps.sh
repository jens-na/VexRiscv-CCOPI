#!/bin/bash

# libisl15
wget http://isl.gforge.inria.fr/isl-0.15.tar.gz
tar -xvf isl-0.15.tar.gz
cd isl-0.15
./configure
make
sudo make install

# RISC-V toolchain (precompiled version)
sudo apt-get install autoconf automake autotools-dev curl device-tree-compiler libmpc-dev libmpfr-dev libgmp-dev gawk build-essential bison flex texinfo gperf libtool patchutils bc zlib1g-dev
curl -O -L "https://github.com/jens-na/riscv-tools-precompiled/blob/master/builds/build-rv32ima.tar.gz.[00-14]?raw=true"
sudo mkdir -p /opt/riscv
sudo cat build-rv32ima.tar.gz.* | sudo tar xzf - -C /opt/riscv

# Verilator
git clone http://git.veripool.org/git/verilator
unset VERILATOR_ROOT
cd verilator
git pull       
git checkout "v3.872"
autoconf        
./configure
make
sudo make install
cd ..

# SpinalHDL
#git clone https://github.com/SpinalHDL/SpinalHDL.git
#cd SpinalHDL
#sbt clean compile publish-local
