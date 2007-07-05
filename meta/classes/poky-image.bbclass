# Common for Poky images
#
# Copyright (C) 2007 OpenedHand LTD

# IMAGE_FEATURES control content of images built with Poky.
# 
# By default we install task-poky-boot and task-base packages - this gives us
# working (console only) rootfs.
#
# Available IMAGE_FEATURES:
#
# - apps-console-core
# - x11-base            - X11 server + minimal desktop	
# - x11-sato            - OpenedHand Sato environment
# - apps-x11-core       - X Terminal, file manager, file editor
# - apps-x11-games
# - apps-x11-pimlico    - OpenedHand Pimlico apps
# - tools-sdk           - SDK
# - tools-debug         - debugging tools
# - tools-profile       - profiling tools
# - tools-testapps      - tools usable to make some device tests
# - nfs-server          - NFS server (exports / over NFS to everybody)
# - dev-pkgs            - development packages
# - dbg-pkgs            - debug packages
#

DISTRO_TASKS += '\
    ${@base_contains("IMAGE_FEATURES", "dbg-pkgs", "task-poky-boot-dbg task-base-dbg", "",d)} \
    ${@base_contains("IMAGE_FEATURES", "dev-pkgs", "task-poky-boot-dev task-base-dev", "",d)} \
    \
    ${@base_contains("IMAGE_FEATURES", "apps-console-core", "task-poky-apps-console", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["apps-console-core", "dbg-pkgs"], "task-poky-apps-console-dbg", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["apps-console-core", "dev-pkgs"], "task-poky-apps-console-dev", "",d)} \
    \
    ${@base_contains("IMAGE_FEATURES", "x11-base", "task-poky-x11-base", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["x11-base", "dbg-pkgs"], "task-poky-x11-base-dbg", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["x11-base", "dev-pkgs"], "task-poky-x11-base-dev", "",d)} \
    \
    ${@base_contains("IMAGE_FEATURES", "x11-sato", "task-poky-x11-sato", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["x11-sato", "dbg-pkgs"], "task-poky-x11-sato-dbg", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["x11-sato", "dev-pkgs"], "task-poky-x11-sato-dev", "",d)} \
    \
    ${@base_contains("IMAGE_FEATURES", "apps-x11-core", "task-poky-apps-x11-core", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["apps-x11-core", "dbg-pkgs"], "task-poky-apps-x11-core-dbg", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["apps-x11-core", "dev-pkgs"], "task-poky-apps-x11-core-dev", "",d)} \
    \
    ${@base_contains("IMAGE_FEATURES", "apps-x11-games", "task-poky-apps-x11-games", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["apps-x11-games", "dbg-pkgs"], "task-poky-apps-x11-games-dbg", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["apps-x11-games", "dev-pkgs"], "task-poky-apps-x11-games-dev", "",d)} \
    \
    ${@base_contains("IMAGE_FEATURES", "apps-x11-pimlico", "task-poky-apps-x11-pimlico", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["apps-x11-pimlico", "dbg-pkgs"], "task-poky-apps-x11-pimlico-dbg", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["apps-x11-pimlico", "dev-pkgs"], "task-poky-apps-x11-pimlico-dev", "",d)} \
    \
    ${@base_contains("IMAGE_FEATURES", "tools-debug", "task-poky-tools-debug", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["tools-debug", "dbg-pkgs"], "task-poky-tools-debug-dbg", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["tools-debug", "dev-pkgs"], "task-poky-tools-debug-dev", "",d)} \
    \
    ${@base_contains("IMAGE_FEATURES", "tools-profile", "task-poky-tools-profile", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["tools-profile", "dbg-pkgs"], "task-poky-tools-profile-dbg", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["tools-profile", "dev-pkgs"], "task-poky-tools-profile-dev", "",d)} \
    \
    ${@base_contains("IMAGE_FEATURES", "tools-testapps", "task-poky-tools-testapps", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["tools-testapps", "dbg-pkgs"], "task-poky-tools-testapps-dbg", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["tools-testapps", "dev-pkgs"], "task-poky-tools-testapps-dev", "",d)} \
    \
    ${@base_contains("IMAGE_FEATURES", "tools-sdk", "task-poky-sdk", "",d)} \    
    ${@base_contains("IMAGE_FEATURES", ["tools-sdk", "dbg-pkgs"], "task-poky-sdk-dbg", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["tools-sdk", "dev-pkgs"], "task-poky-sdk-dev", "",d)} \
    \
    ${@base_contains("IMAGE_FEATURES", "nfs-server", "task-poky-nfs-server", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["nfs-server", "dbg-pkgs"], "task-poky-nfs-server-dbg", "",d)} \
    ${@base_contains("IMAGE_FEATURES", ["nfs-server", "dev-pkgs"], "task-poky-nfs-server-dev", "",d)} \
    '

IMAGE_INSTALL ?= "${DISTRO_TASKS}"

X11_IMAGE_FEATURES  = "x11-base apps-x11-core"
SATO_IMAGE_FEATURES = "${X11_IMAGE_FEATURES} apps-x11-sato apps-x11-games apps-x11-pimlico"

inherit image
