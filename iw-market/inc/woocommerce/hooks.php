<?php
/**
 * WooCommerce hooks.
 *
 * @package IW_Market
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

add_filter( 'woocommerce_product_thumbnails', 'iw_market_product_gallery_wrapper', 10, 2 );

/**
 * Wrap product gallery for styling.
 *
 * @param string $html Gallery HTML.
 * @param int    $post_id Post ID.
 * @return string
 */
function iw_market_product_gallery_wrapper( $html, $post_id ) {
	if ( ! $html ) {
		return $html;
	}

	return '<div class="iw-product-gallery">' . $html . '</div>';
}

add_action( 'woocommerce_after_single_product', 'iw_market_recently_viewed' );

/**
 * Output recently viewed products.
 */
function iw_market_recently_viewed() {
	if ( ! function_exists( 'woocommerce_recently_viewed_products' ) ) {
		return;
	}

	woocommerce_recently_viewed_products( array( 'columns' => 4 ) );
}
