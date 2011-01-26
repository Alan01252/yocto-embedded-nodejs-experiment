inherit cross

BASE_PACKAGE_ARCH = "${SDK_ARCH}"
PACKAGE_ARCH = "${BASE_PACKAGE_ARCH}"
STAGING_DIR_TARGET = "${STAGING_DIR}/${SDK_ARCH}-nativesdk${SDK_VENDOR}-${SDK_OS}"

TARGET_ARCH = "${SDK_ARCH}"
TARGET_VENDOR = "${SDK_VENDOR}"
TARGET_OS = "${SDK_OS}"
TARGET_PREFIX = "${SDK_PREFIX}"
TARGET_CC_ARCH = "${SDK_CC_ARCH}"

target_libdir = "${SDKPATHNATIVE}${libdir_nativesdk}"
target_includedir = "${SDKPATHNATIVE}${includedir_nativesdk}"
target_base_libdir = "${SDKPATHNATIVE}${base_libdir_nativesdk}"
target_prefix = "${SDKPATHNATIVE}${prefix_nativesdk}"
target_exec_prefix = "${SDKPATHNATIVE}${exec_prefix_nativesdk}"

do_populate_sysroot[stamp-extra-info] = ""
do_package[stamp-extra-info] = ""
