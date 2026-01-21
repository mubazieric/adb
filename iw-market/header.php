<?php
/**
 * Header template.
 *
 * @package IW_Market
 */

?><!DOCTYPE html>
<html <?php language_attributes(); ?>>
<head>
	<meta charset="<?php bloginfo( 'charset' ); ?>">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<?php wp_head(); ?>
</head>
<body <?php body_class(); ?>>
<?php wp_body_open(); ?>
<div class="iw-site">
	<header class="iw-header <?php echo esc_attr( get_theme_mod( 'iw_market_sticky_header', true ) ? 'is-sticky' : '' ); ?>">
		<div class="iw-header__inner">
			<div class="iw-brand">
				<?php
				if ( has_custom_logo() ) {
					the_custom_logo();
				} else {
					bloginfo( 'name' );
				}
				?>
			</div>
			<nav class="iw-nav" aria-label="<?php echo esc_attr__( 'Primary navigation', 'iw-market' ); ?>">
				<?php
				wp_nav_menu(
					array(
						'theme_location' => 'primary',
						'container'      => false,
						'fallback_cb'    => '__return_false',
					)
				);
				?>
			</nav>
			<a class="iw-button" href="<?php echo esc_url( wc_get_cart_url() ); ?>">
				<?php echo esc_html__( 'Cart', 'iw-market' ); ?>
			</a>
		</div>
	</header>

	<div class="iw-search-modal" role="dialog" aria-modal="true">
		<div class="iw-search-modal__panel">
			<label for="iw-search-input" class="screen-reader-text">
				<?php echo esc_html__( 'Search products', 'iw-market' ); ?>
			</label>
			<input id="iw-search-input" type="search" placeholder="<?php echo esc_attr__( 'Search products', 'iw-market' ); ?>" />
			<div class="iw-search-results" aria-live="polite"></div>
		</div>
	</div>
	<main class="iw-main">
