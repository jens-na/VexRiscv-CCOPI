#!/bin/bash

# RISC-V toolchain dependencies
sudo apt-get install autoconf automake autotools-dev curl device-tree-compiler libmpc-dev libmpfr-dev libgmp-dev gawk build-essential bison flex texinfo gperf libtool patchutils bc zlib1g-dev

# RISC-V toolchain
git clone https://github.com/riscv/riscv-tools.git
cd riscv-tools
git submodule update --init --recursive
export RISCV=/opt/riscv
./build-rv32ima.sh
cd ..

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
