<?php
/**
 * Plugin Name: IW Market Core
 * Description: Companion plugin for IW Market theme. Provides demo importer and patterns.
 * Version: 1.0.0
 * Author: IWAT Digital
 * Text Domain: iw-market-core
 * License: GPL-2.0+
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

define( 'IW_MARKET_CORE_PATH', plugin_dir_path( __FILE__ ) );

require_once IW_MARKET_CORE_PATH . 'inc/demo-importer.php';
require_once IW_MARKET_CORE_PATH . 'inc/patterns.php';
