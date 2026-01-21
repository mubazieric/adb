<?php
/**
 * Register block patterns.
 *
 * @package IW_Market_Core
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

add_action( 'init', 'iw_market_core_register_patterns' );

/**
 * Register patterns.
 */
function iw_market_core_register_patterns() {
	register_block_pattern(
		'iw-market/hero',
		array(
			'title'       => __( 'IW Market Hero', 'iw-market-core' ),
			'categories'  => array( 'featured' ),
			'content'     => '<!-- wp:group {"align":"wide","style":{"spacing":{"padding":{"top":"60px","bottom":"60px"}}}} --><div class="wp-block-group alignwide" style="padding-top:60px;padding-bottom:60px"><!-- wp:heading {"textAlign":"center","level":1} --><h1 class="wp-block-heading has-text-align-center">Premium Marketplace Experience</h1><!-- /wp:heading --><!-- wp:paragraph {"align":"center"} --><p style="text-align:center">Launch your Uganda-focused marketplace with IW Market.</p><!-- /wp:paragraph --></div><!-- /wp:group -->',
		)
	);
}
