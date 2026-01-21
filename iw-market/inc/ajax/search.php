<?php
/**
 * AJAX search handlers.
 *
 * @package IW_Market
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

add_action( 'wp_ajax_iw_market_search', 'iw_market_ajax_search' );
add_action( 'wp_ajax_nopriv_iw_market_search', 'iw_market_ajax_search' );

/**
 * AJAX search.
 */
function iw_market_ajax_search() {
	check_ajax_referer( 'iw_market_nonce', 'nonce' );

	$term = isset( $_GET['term'] ) ? sanitize_text_field( wp_unslash( $_GET['term'] ) ) : '';

	if ( '' === $term ) {
		wp_send_json_success( array() );
	}

	$query = new WP_Query(
		array(
			'post_type'      => array( 'product', 'product_variation' ),
			'post_status'    => 'publish',
			's'              => $term,
			'posts_per_page' => 6,
		)
	);

	$results = array();

	while ( $query->have_posts() ) {
		$query->the_post();
		$results[] = array(
			'id'    => get_the_ID(),
			'title' => get_the_title(),
			'url'   => get_permalink(),
		);
	}

	wp_reset_postdata();
	wp_send_json_success( $results );
}
