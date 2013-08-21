# Based on: https://github.com/gumstix/meta-gumstix-extras/blob/dylan/recipes-images/gumstix/gumstix-console-image.bb
DESCRIPTION = "An image with e-CAM56 37x GSTIX and TI DSP drivers"
LICENSE = "MIT"
PR = "r0"

IMAGE_FEATURES += "package-management ssh-server-openssh tools-sdk"
IMAGE_LINGUAS = "en-us"

inherit core-image

# Gumstix machines individually RDEPEND on the firware they need but we repeat
# it here as we might want to use the same image on multiple different machines.
FIRMWARE_INSTALL = " \
linux-firmware-sd8686 \
linux-firmware-sd8787 \
"

TOOLS_INSTALL = " \
alsa-utils \
systemd-analyze \
cpufrequtils \
grep \
gzip \
iputils \
iw \
memtester \
nano \
ntp \
sudo \
tar \
tslib \
u-boot-mkimage \
u-boot-fw-utils \
vim \
wget \
zip \
"

ECAM_INSTALL = " \
ecam-driver \
gstreamer-ti \
gst-ffmpeg \
gst-plugins-good \
gst-plugins-good-video4linux2 \
gst-plugins-good-video4linux2-dev \
gst-meta-video \
gst-meta-audio \
gst-meta-debug \
opencv \
opencv-dev \
opencv-apps \
"

IMAGE_INSTALL += " \
packagegroup-cli-tools \
packagegroup-cli-tools-debug \
${FIRMWARE_INSTALL} \
${TOOLS_INSTALL} \
${ECAM_INSTALL} \
"

set_gumstix_user() {
    echo "gumstix:x:500:" >> "${IMAGE_ROOTFS}/etc/group"
    echo "gumstix:VQ43An5F8LYqc:500:500:Gumstix User,,,:/home/gumstix:/bin/bash"  >> "${IMAGE_ROOTFS}/etc/passwd"

    install -d "${IMAGE_ROOTFS}/home/gumstix"
    cp -f "${IMAGE_ROOTFS}/etc/skel/.bashrc" "${IMAGE_ROOTFS}/etc/skel/.profile" "${IMAGE_ROOTFS}/home/gumstix"
    chown gumstix:gumstix -R "${IMAGE_ROOTFS}/home/gumstix"

    echo "%gumstix ALL=(ALL) ALL" >> "${IMAGE_ROOTFS}/etc/sudoers"
    chmod 0440 "${IMAGE_ROOTFS}/etc/sudoers"
    chmod u+s "${IMAGE_ROOTFS}/usr/bin/sudo"
}

enable_services() {
    install -d "${IMAGE_ROOTFS}/etc/systemd/system/multi-user.target.wants"

    ln -sf "/lib/systemd/system/ecam-driver.service" \
        "${IMAGE_ROOTFS}/etc/systemd/system/multi-user.target.wants/ecam-driver.service"
    ln -sf "/lib/systemd/system/gstti-init.service" \
        "${IMAGE_ROOTFS}/etc/systemd/system/multi-user.target.wants/gstti-init.service"
}

ROOTFS_POSTPROCESS_COMMAND =+ "set_gumstix_user ; enable_services ; "
