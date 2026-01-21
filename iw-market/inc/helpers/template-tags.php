<?php
/**
 * Template tags.
 *
 * @package IW_Market
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

/**
 * Output mobile bottom nav.
 */
function iw_market_mobile_nav() {
	?>
	<nav class="iw-mobile-nav" aria-label="<?php echo esc_attr__( 'Mobile navigation', 'iw-market' ); ?>">
		<a href="<?php echo esc_url( home_url( '/' ) ); ?>">
			<?php echo esc_html__( 'Home', 'iw-market' ); ?>
		</a>
		<a href="<?php echo esc_url( wc_get_page_permalink( 'shop' ) ); ?>">
			<?php echo esc_html__( 'Categories', 'iw-market' ); ?>
		</a>
		<a href="#" class="iw-search-trigger">
			<?php echo esc_html__( 'Search', 'iw-market' ); ?>
		</a>
		<a href="<?php echo esc_url( wc_get_cart_url() ); ?>">
			<?php echo esc_html__( 'Cart', 'iw-market' ); ?>
		</a>
		<a href="<?php echo esc_url( wc_get_page_permalink( 'myaccount' ) ); ?>">
			<?php echo esc_html__( 'Account', 'iw-market' ); ?>
		</a>
	</nav>
	<?php
}
