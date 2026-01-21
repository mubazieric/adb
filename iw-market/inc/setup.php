<?php
/**
 * Theme setup.
 *
 * @package IW_Market
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

add_action( 'after_setup_theme', 'iw_market_setup' );

/**
 * Register theme supports.
 */
function iw_market_setup() {
	load_theme_textdomain( 'iw-market', get_template_directory() . '/languages' );

	add_theme_support( 'title-tag' );
	add_theme_support( 'post-thumbnails' );
	add_theme_support( 'responsive-embeds' );
	add_theme_support( 'editor-styles' );
	add_theme_support( 'woocommerce' );
	add_theme_support( 'align-wide' );
	add_theme_support( 'custom-logo', array( 'height' => 40, 'width' => 160 ) );

	register_nav_menus(
		array(
			'primary' => __( 'Primary Menu', 'iw-market' ),
			'top'     => __( 'Top Bar Menu', 'iw-market' ),
			'footer'  => __( 'Footer Menu', 'iw-market' ),
		)
	);
}
