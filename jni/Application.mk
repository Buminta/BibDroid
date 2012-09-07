# Fixes "unresolvable R_ARM_THM_CALL relocation against symbol" build error
# More info at http://stackoverflow.com/questions/12177561/using-mupdf-ndk-build-error
NDK_TOOLCHAIN_VERSION=4.4.3

# The ARMv7 is significanly faster due to the use of the hardware FPU
APP_PLATFORM=android-8
APP_ABI := armeabi armeabi-v7a
APP_OPTIM := debug
