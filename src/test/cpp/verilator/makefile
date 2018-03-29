IBUS?=SIMPLE
DBUS?=SIMPLE
TRACE?=yes
TRACE_ACCESS?=no
TRACE_START=0
ISA_TEST?=no
MUL?=no
DIV?=no
CSR?=yes
MMU?=no
DEBUG_PLUGIN?=no
DEBUG_PLUGIN_EXTERNAL?=no
CUSTOM_SIMD_ADD?=no
DHRYSTONE=no
FREERTOS=no
REDO?=1
REF=no
TRACE_WITH_TIME=no
REF_TIME=no
THREAD_COUNT=4

ADDCFLAGS += -CFLAGS -DIBUS_${IBUS}
ADDCFLAGS += -CFLAGS -DDBUS_${DBUS}
ADDCFLAGS += -CFLAGS -DREDO=${REDO}
ADDCFLAGS += -CFLAGS -pthread

ADDCFLAGS += -CFLAGS -DTHREAD_COUNT=${THREAD_COUNT}
ifeq ($(DHRYSTONE),yes)
	ADDCFLAGS += -CFLAGS -DDHRYSTONE
endif

ifeq ($(TRACE),yes)
	VERILATOR_ARGS += --trace
	ADDCFLAGS += -CFLAGS -DTRACE
endif

ifeq ($(CSR),yes)
	ADDCFLAGS += -CFLAGS -DCSR
endif

ifeq ($(CUSTOM_SIMD_ADD),yes)
	ADDCFLAGS += -CFLAGS -DCUSTOM_SIMD_ADD
endif

ifeq ($(CUSTOM_CRYACCEL), yes)
	ADDCFLAGS += -CFLAGS -DCUSTOM_CRYACCEL
endif

ifeq ($(TRACE_WITH_TIME),yes)
	ADDCFLAGS += -CFLAGS -DTRACE_WITH_TIME
endif

ifeq ($(REF_TIME),yes)
	ADDCFLAGS += -CFLAGS -DREF_TIME
endif

ifeq ($(ISA_TEST),yes)
	ADDCFLAGS += -CFLAGS -DISA_TEST
endif

ifeq ($(MMU),yes)
	ADDCFLAGS += -CFLAGS -DMMU
endif

ifeq ($(MUL),yes)
	ADDCFLAGS += -CFLAGS -DMUL
endif

ifeq ($(DIV),yes)
	ADDCFLAGS += -CFLAGS -DDIV
endif

ifeq ($(TRACE_ACCESS),yes)
	ADDCFLAGS += -CFLAGS -DTRACE_ACCESS
endif

ifneq ($(DEBUG_PLUGIN),no)
	ADDCFLAGS += -CFLAGS -DDEBUG_PLUGIN
	ADDCFLAGS += -CFLAGS -DDEBUG_PLUGIN_${DEBUG_PLUGIN}
endif

ifeq ($(DEBUG_PLUGIN_EXTERNAL),yes)
	ADDCFLAGS += -CFLAGS -DDEBUG_PLUGIN_EXTERNAL
endif

ifeq ($(REF),yes)
	ADDCFLAGS += -CFLAGS -DREF
endif

ADDCFLAGS += -CFLAGS -DTRACE_START=${TRACE_START}
ifeq ($(FREERTOS),yes)
	ADDCFLAGS += -CFLAGS -DFREERTOS
endif

all: clean custom_tests run

run: compile
	./obj_dir/VVexRiscv

verilate:
	verilator -cc  ../../../../VexRiscv.v  -O3 -CFLAGS -std=c++11 -LDFLAGS -pthread  ${ADDCFLAGS} --gdbbt ${VERILATOR_ARGS} -Wno-WIDTH --debug --x-assign unique --exe main.cpp
 	
compile: verilate
	make  -j  -C obj_dir/ -f VVexRiscv.mk VVexRiscv
 	
custom_tests:
	cd ../custom/ && make

clean:
	rm -rf obj_dir
 	
