require linux-mx31.inc

PR = "r3"

FILESDIR = "${WORKDIR}"

SRC_URI = "${KERNELORG_MIRROR}pub/linux/kernel/v2.6/linux-2.6.19.2.tar.bz2 \
           file://defconfig-mx31litekit \
           file://defconfig-mx31ads \
	   "

SRC_URI_append_mx31litekit = " \
           file://linux-2.6.19.2-mx3lite.patch.gz \
           file://linux-2.6.19.2-mx3lite.patch;patch=1 \
           file://mx31lite-boot.patch;patch=1 \
           file://mx31lite-fb.patch;patch=1 \
           file://mx31lite-spi.patch;patch=1 \
           "

SRC_URI_append_mx31ads = " \
           file://mx31ads-patches.tgz \
           file://linux-2.6.19.2-mx-arch_arm.patch;patch=1 \
           file://linux-2.6.19.2-mx-arm_oprofile.patch;patch=1 \
           file://linux-2.6.19.2-mx-codetest.patch;patch=1 \
           file://linux-2.6.19.2-mx-drivers_char.patch;patch=1 \
           file://linux-2.6.19.2-mx-drivers_i2c.patch;patch=1 \
           file://linux-2.6.19.2-mx-drivers_ide.patch;patch=1 \
           file://linux-2.6.19.2-mx-drivers_input.patch;patch=1 \
           file://linux-2.6.19.2-mx-drivers_media.patch;patch=1 \
           file://linux-2.6.19.2-mx-drivers_mmc.patch;patch=1 \
           file://linux-2.6.19.2-mx-drivers_mtd.patch;patch=1 \
           file://linux-2.6.19.2-mx-drivers_mxc-2.patch;patch=1 \
           file://linux-2.6.19.2-mx-drivers_net-2.patch;patch=1 \
           file://linux-2.6.19.2-mx-drivers_pcmcia.patch;patch=1 \
           file://linux-2.6.19.2-mx-drivers_serial.patch;patch=1 \
           file://linux-2.6.19.2-mx-drivers_spi.patch;patch=1 \
           file://linux-2.6.19.2-mx-drivers_usb.patch;patch=1 \
           file://linux-2.6.19.2-mx-drivers_video-2.patch;patch=1 \
           file://linux-2.6.19.2-mx-drivers_w1-2.patch;patch=1 \
           file://linux-2.6.19.2-mx-include_mxc.patch;patch=1 \
           file://linux-2.6.19.2-mx-mach_mx27-2.patch;patch=1 \
           file://linux-2.6.19.2-mx-mach_mx31.patch;patch=1 \
           file://linux-2.6.19.2-mx-plat_mxc.patch;patch=1 \
           file://linux-2.6.19.2-mx-sound-2.patch;patch=1 \
           file://linux-2.6.19.2-mx-irttp_reserve.patch;patch=1 \
           file://linux-2.6.19.2-mx-porting-3.patch;patch=1 \
           file://linux-2.6.19.2-mx-fsl_logos.patch;patch=1 \
           file://linux-2.6.19.2-mx-remove_kfi.patch;patch=1 \
           file://linux-2.6.19.2-mx-touchscreen_fix.patch;patch=1 \
           file://linux-2.6.19.2-mx-xip_boot_with_nor.patch;patch=1 \
           file://linux-2.6.19.2-mx-xip_with_nand.patch;patch=1 \
           file://linux-2.6.19.2-mx-alsa_playback_ata_corruption.patch;patch=1 \
           file://linux-2.6.19.2-mx-sdma_channel_priority.patch;patch=1 \
           file://linux-2.6.19.2-mx-dpm.patch;patch=1 \
           file://linux-2.6.19.2-mx-voice_codec_noise.patch;patch=1 \
           file://linux-2.6.19.2-mx-mx27_hrt.patch;patch=1 \
           file://linux-2.6.19.2-mx-ata_clk_fix.patch;patch=1 \
           file://linux-2.6.19.2-mx-ts_suspend_fix.patch;patch=1 \
           file://linux-2.6.19.2-mx-i2c_stop_fix.patch;patch=1 \
           file://linux-2.6.19.2-mx-mc521da_camera.patch;patch=1 \
           file://linux-2.6.19.2-mx-ipu_pm_sleep.patch;patch=1 \
           file://linux-2.6.19.2-mx-warns_fix.patch;patch=1 \
           file://linux-2.6.19.2-mx-vpu_buf_list.patch;patch=1 \
           file://linux-2.6.19.2-mx-wdt_kconfig_fix.patch;patch=1 \
           file://linux-2.6.19.2-mx-arm926_dcache_writethrough_flush_fix.patch;patch=1;pnum=0 \
           file://linux-2.6.19.2-mx-ide_dma_fix.patch;patch=1 \
           file://linux-2.6.19.2-mx-focus453_reset_fix.patch;patch=1 \
           file://linux-2.6.19.2-mx-irda_mode_fix.patch;patch=1 \
           file://linux-2.6.19.2-mx-watchdog_fix.patch;patch=1 \
           file://linux-2.6.19.2-mx-fec_loadable_module.patch;patch=1 \
           file://linux-2.6.19.2-mx-fb_loadable_module.patch;patch=1 \
           file://linux-2.6.19.2-mx-mx27_warns_fix.patch;patch=1 \
           file://linux-2.6.19.2-mx-add_rc_checking_on_copy_to_user.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_nand_two-read_stats_cmd.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_convity_multiple_open_mode.patch;patch=1 \
           file://linux-2.6.19.2-mx-csi_dma_si_capture.patch;patch=1 \
           file://linux-2.6.19.2-mx-new_mx27_dptc_table.patch;patch=1 \
           file://linux-2.6.19.2-mx-8250_dropped_tx_chars-2.patch;patch=1 \
           file://linux-2.6.19.2-mx-video_rotate_mirror.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_audio_sequencing.patch;patch=1 \
           file://linux-2.6.19.2-mx-rid_offset_asm_macros.patch;patch=1 \
           file://linux-2.6.19.2-mx-remove_mxc_err_chk.patch;patch=1 \
           file://linux-2.6.19.2-mx-v4l2_to_right_framebuffer.patch;patch=1 \
           file://linux-2.6.19.2-mx-v4l2_tearing_fix.patch;patch=1 \
           file://linux-2.6.19.2-mx-initialize_initdata_fix.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_mx27_printks.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_disconnect_event_with_pin_detect.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_fec_dcache_range_args.patch;patch=1 \
           file://linux-2.6.19.2-mx-i2c_plat_cleanup.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_mono_output.patch;patch=1 \
           file://linux-2.6.19.2-mx-add_12k_playback_support.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_irqreturn_t_arguments.patch;patch=1 \
           file://linux-2.6.19.2-mx-remove_erroneous_semicolons.patch;patch=1 \
           file://linux-2.6.19.2-mx-port_ctswic_h.patch;patch=1 \
           file://linux-2.6.19.2-mx-use_std_clk_api.patch;patch=1 \
           file://linux-2.6.19.2-mx-mx31_4_step_dvfs_support.patch;patch=1 \
           file://linux-2.6.19.2-mx-remove_mx31_wfi_workaround.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_sdma_events_table.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_vpu_debugging_printks.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_vpu_dma_free_coherent.patch;patch=1 \
           file://linux-2.6.19.2-mx-mxcuart_shutdown_cleanup.patch;patch=1 \
           file://linux-2.6.19.2-mx-1wire_search_rom_accel.patch;patch=1 \
           file://linux-2.6.19.2-mx-ss08_sdma_script_update.patch;patch=1 \
           file://linux-2.6.19.2-mx-workaround_for_sdma_chn10_access.patch;patch=1 \
           file://linux-2.6.19.2-mx-add_1wire_platform_data.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_serial_core_dumping_mctl_status.patch;patch=1 \
           file://linux-2.6.19.2-mx-mx31_vga_mpeg4_support-2.patch;patch=1 \
           file://linux-2.6.19.2-mx-change_mmc_prescaler_calc.patch;patch=1 \
           file://linux-2.6.19.2-mx-mc521da_camera_fix.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_mx31_sr_mode_after_csi.patch;patch=1 \
           file://linux-2.6.19.2-mx-mx31_sr_mode_broken_after_dma_access.patch;patch=1 \
           file://linux-2.6.19.2-mx-unify_light_ioctl.patch;patch=1 \
           file://linux-2.6.19.2-mx-use_wtg_srs_for_arch_reset.patch;patch=1 \
           file://linux-2.6.19.2-mx-wfi_workaround.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_mx27_gpio_sensor.patch;patch=1 \
           file://linux-2.6.19.2-mx-audio_support_changes.patch;patch=1 \
           file://linux-2.6.19.2-mx-add_rngc_support.patch;patch=1 \
           file://linux-2.6.19.2-mx-mxc_ir_indent_fix.patch;patch=1 \
           file://linux-2.6.19.2-mx-v4l_loopback_demo_fix.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_security_debug_printks.patch;patch=1 \
           file://linux-2.6.19.2-mx-update_copyrights-2.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_rng_doxygen_warnings.patch;patch=1 \
           file://linux-2.6.19.2-mx-remove_fir_tx_sdma_workaround.patch;patch=1 \
           file://linux-2.6.19.2-mx-mxc_debug_leds.patch;patch=1 \
           file://linux-2.6.19.2-mx-add_gpio_function_camera_resume.patch;patch=1 \
           file://linux-2.6.19.2-mx-mxc_pf_prevent_multiple_opens.patch;patch=1 \
           file://linux-2.6.19.2-mx-turn_off_unused_clocks_on_boot.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_mx27_vpu_clock_enable_disable.patch;patch=1 \
           file://linux-2.6.19.2-mx-usb_clk_off_on_ulpi.patch;patch=1 \
           file://linux-2.6.19.2-mx-sdma_ipcv2.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_csi_clk_calc_for_mx31.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_gadget_dtd_allocation.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_wfi_workaround.patch;patch=1 \
           file://linux-2.6.19.2-mx-isp1301_i2c.patch;patch=1 \
           file://linux-2.6.19.2-mx-mx31_enable_iim_clock.patch;patch=1 \
           file://linux-2.6.19.2-mx-add_1wire_enable_disable.patch;patch=1 \
           file://linux-2.6.19.2-mx-add_rng_clock_enable_disable.patch;patch=1 \
           file://linux-2.6.19.2-mx-remove_wfi_workaround.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_mx2fb_clock_for_tv_out.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_hacc_clocking.patch;patch=1 \
           file://linux-2.6.19.2-mx-1wire_boot_delay.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_mxc_gettimeoffset_div0.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_generation_of_csi_mclk.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_rs232_cea936_handler_issue.patch;patch=1 \
           file://linux-2.6.19.2-mx-fix_usb_invalid_printks.patch;patch=1 \
           file://linux-2.6.19.2-mx-enable_mbx_clock_at_boot.patch;patch=1 \
           file://linux-2.6.19.2-mx-clocking_update_pcmcia.patch;patch=1 \
           file://linux-2.6.19.2-mx-combine_mx27_mx31_usb_headers-2.patch;patch=1 \
           file://linux-2.6.19.2-mx-unified_doxygen.patch;patch=1 \
           file://linux-2.6.19.2-mx-L2618-2_scrub.patch;patch=1 \
           file://linux-2.6.19.2-mx-add_kern_info_to_printks-2.patch;patch=1 \
           file://linux-2.6.19.2-mx-generic_clk_porting.patch;patch=1 \
           file://linux-2.6.19.2-mx-code_review_fixes_following_L2618.2-4.patch;patch=1 \
           file://linux-2.6.19.2-mx-yaffs2_filesystem.patch;patch=1;pnum=0 \
           file://linux-2.6.19.2-mx-yaffs2_define_min_fix.patch;patch=1;pnum=0 \
           file://linux-2.6.19.2-mx-add_mt9v111_camera_detection.patch;patch=1;pnum=0 \
           file://linux-2.6.19.2-mx-fix_i2c_adapter_name.patch;patch=1;pnum=0 \
           file://linux-2.6.19.2-mx-mx31_enable_2nd_mmc_slot.patch;patch=1 \
           file://linux-2.6.19.2-mx-dvfs_compile_fixes.patch;patch=1 \
           file://linux-2.6.19.2-mx-mmc_sdhc.patch;patch=1 \
           file://linux-2.6.19.2-mx-spi_inactive.patch;patch=1 \
           file://linux-2.6.19.2-mx-reserve_mx3_uart2_for_fir.patch;patch=1 \
           file://linux-2.6.19.2-mx31_TO2_reg_swizzle_workaround.patch;patch=1 \
           file://linux-2.6.19.2-mx31_suspend_resumes_on_eth_activity.patch;patch=1 \
           "

S = "${WORKDIR}/linux-2.6.19.2"
