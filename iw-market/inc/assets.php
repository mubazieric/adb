<?php
/**
 * Enqueue theme assets.
 *
 * @package IW_Market
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

add_action( 'wp_enqueue_scripts', 'iw_market_enqueue_assets' );

/**
 * Enqueue styles and scripts.
 */
function iw_market_enqueue_assets() {
	$theme = wp_get_theme();

	wp_enqueue_style( 'iw-market-style', get_stylesheet_uri(), array(), $theme->get( 'Version' ) );
	wp_enqueue_style( 'iw-market-base', get_template_directory_uri() . '/assets/css/base.css', array(), $theme->get( 'Version' ) );

	wp_enqueue_script( 'iw-market-main', get_template_directory_uri() . '/assets/js/main.js', array(), $theme->get( 'Version' ), true );

	wp_localize_script(
		'iw-market-main',
		'iwMarket',
		array(
			'ajaxUrl'   => admin_url( 'admin-ajax.php' ),
			'nonce'     => wp_create_nonce( 'iw_market_nonce' ),
			'texts'     => array(
				'searching' => __( 'Searching…', 'iw-market' ),
			),
		)
	);
}
