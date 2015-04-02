# Processes poky-ref-manual and yocto-project-qs manual (<word>-<word>-<word> style).
# This style is for manual folders like "yocto-project-qs" and "poky-ref-manual".
# This is the old way that did it.  Can't do that now that we have "bitbake-user-manual" strings
# in the mega-manual.
# s/\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/[a-z]*-[a-z]*-[a-z]*\/[a-z]*-[a-z]*-[a-z]*.html#/\"link\" href=\"#/g
s/\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/yocto-project-qs\/yocto-project-qs.html#/\"link\" href=\"#/g
s/\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/poky-ref-manual\/poky-ref-manual.html#/\"link\" href=\"#/g

# Processes all other manuals (<word>-<word> style) except for the BitBake User Manual because
# it is not included in the mega-manual.
# This style is for manual folders that use two word, which is the standard now (e.g. "ref-manual").
# This was the one-liner that worked before we introduced the BitBake User Manual, which is
# not in the mega-manual.
# s/\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/[a-z]*-[a-z]*\/[a-z]*-[a-z]*.html#/\"link\" href=\"#/g

s/\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/adt-manual\/adt-manual.html#/\"link\" href=\"#/g
s/\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/bsp-guide\/bsp-guide.html#/\"link\" href=\"#/g
s/\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/dev-manual\/dev-manual.html#/\"link\" href=\"#/g
s/\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/kernel-dev\/kernel-dev.html#/\"link\" href=\"#/g
s/\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/profile-manual\/profile-manual.html#/\"link\" href=\"#/g
s/\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/ref-manual\/ref-manual.html#/\"link\" href=\"#/g
s/\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/toaster-manual\/toaster-manual.html#/\"link\" href=\"#/g
s/\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/yocto-project-qs\/yocto-project-qs.html#/\"link\" href=\"#/g

# Process cases where just an external manual is referenced without an id anchor
s/<a class=\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/yocto-project-qs\/yocto-project-qs.html\" target=\"_top\">Yocto Project Quick Start<\/a>/Yocto Project Quick Start/g
s/<a class=\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/dev-manual\/dev-manual.html\" target=\"_top\">Yocto Project Development Manual<\/a>/Yocto Project Development Manual/g
s/<a class=\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/adt-manual\/adt-manual.html\" target=\"_top\">Yocto Project Application Developer's Guide<\/a>/Yocto Project Application Developer's Guide/g
s/<a class=\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/bsp-guide\/bsp-guide.html\" target=\"_top\">Yocto Project Board Support Package (BSP) Developer's Guide<\/a>/Yocto Project Board Support Package (BSP) Developer's Guide/g
s/<a class=\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/profile-manual\/profile-manual.html\" target=\"_top\">Yocto Project Profiling and Tracing Manual<\/a>/Yocto Project Profiling and Tracing Manual/g
s/<a class=\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/kernel-dev\/kernel-dev.html\" target=\"_top\">Yocto Project Linux Kernel Development Manual<\/a>/Yocto Project Linux Kernel Development Manual/g
s/<a class=\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/ref-manual\/ref-manual.html\" target=\"_top\">Yocto Project Reference Manual<\/a>/Yocto Project Reference Manual/g
s/<a class=\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/1.8\/toaster-manual\/toaster-manual.html\" target=\"_top\">Yocto Project Toaster User Manual<\/a>/Yocto Project Toaster User Manual/g
