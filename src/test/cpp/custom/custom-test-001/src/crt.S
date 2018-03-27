.globl _start
_start:

#define r_type_insn(_f7, _rs2, _rs1, _f3, _rd, _opc) \
.word (((_f7) << 25) | ((_rs2) << 20) | ((_rs1) << 15) | ((_f3) << 12) | ((_rd) << 7) | ((_opc) << 0))

#define ccopicmd0(_rd, _rs1, _rs2) \
r_type_insn(0b0000000, _rs2, _rs1, 0b000, _rd, 0b0001011)

#define ccopi_type_insn(_f2, _f5, _rs2, _rs1, _f3, _rd, _opc) \
.word ( ((_f2) << 30) | ((_f5) << 25) | ((_rs2) << 20) | ((_rs1) << 15) | ((_f3) << 12) | ((_rd) << 7) | ((_opc) << 0))

#define ccopicmd1(_rd, _rs1, _rs2, _iaddr) \
ccopi_type_insn(0b00, _iaddr, _rs2, _rs1, 0b001, _rd, 0b0001011)

start:
    # Test CoProcessor Ex0
    #li x4, 0x1234ffff
    #li x5, 0x5678ffff

    #ccopicmd0(0x6, 0x4, 0x5)
    
    #li x5, 0x68adfffe // 0x1234ffff + 0x5678ffff 
    #bne x6, x5, fail

    # Test CoProcessor Ex1
    li x4, 0x11
    li x5, 0x22
    
    ccopicmd1(0x6, 0x4, 0x5, 0x2)

    li x5, 0x93 // 0x11 + 0x22 + 0x60 = 0x93
    bne x6, x5, fail

    
    j pass

fail: //x28 => error code
    li x2, 0xF00FFF24
    sw x28, 0(x2)

pass:
    li x2, 0xF00FFF20
    sw x0, 0(x2)

    nop
    nop
    nop
    nop
    nop
    nop
