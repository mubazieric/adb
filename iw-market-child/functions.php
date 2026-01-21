<?php
/**
 * IW Market child theme functions.
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

add_action( 'wp_enqueue_scripts', 'iw_market_child_enqueue_styles', 20 );

/**
 * Enqueue child theme styles.
 */
function iw_market_child_enqueue_styles() {
	wp_enqueue_style( 'iw-market-child', get_stylesheet_uri(), array( 'iw-market-style' ), '1.0.0' );
}
