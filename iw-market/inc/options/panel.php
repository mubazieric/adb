<?php
/**
 * Lightweight theme options panel.
 *
 * @package IW_Market
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

add_action( 'admin_menu', 'iw_market_register_options_page' );
add_action( 'admin_init', 'iw_market_register_settings' );

/**
 * Register the options page.
 */
function iw_market_register_options_page() {
	add_theme_page(
		__( 'IW Market Options', 'iw-market' ),
		__( 'IW Market Options', 'iw-market' ),
		'manage_options',
		'iw-market-options',
		'iw_market_render_options_page'
	);
}

/**
 * Register settings.
 */
function iw_market_register_settings() {
	register_setting( 'iw_market_options', 'iw_market_options', 'iw_market_sanitize_options' );

	add_settings_section(
		'iw_market_general',
		__( 'General Settings', 'iw-market' ),
		'__return_false',
		'iw-market-options'
	);

	add_settings_field(
		'enable_quick_view',
		__( 'Enable Quick View', 'iw-market' ),
		'iw_market_checkbox_field',
		'iw-market-options',
		'iw_market_general',
		array(
			'label' => 'enable_quick_view',
		)
	);

	add_settings_field(
		'enable_wishlist',
		__( 'Enable Wishlist', 'iw-market' ),
		'iw_market_checkbox_field',
		'iw-market-options',
		'iw_market_general',
		array(
			'label' => 'enable_wishlist',
		)
	);

	add_settings_field(
		'enable_compare',
		__( 'Enable Compare', 'iw-market' ),
		'iw_market_checkbox_field',
		'iw-market-options',
		'iw_market_general',
		array(
			'label' => 'enable_compare',
		)
	);
}

/**
 * Sanitize theme options.
 *
 * @param array $options Submitted options.
 * @return array
 */
function iw_market_sanitize_options( $options ) {
	$options = is_array( $options ) ? $options : array();

	return array(
		'enable_quick_view' => ! empty( $options['enable_quick_view'] ) ? 1 : 0,
		'enable_wishlist'   => ! empty( $options['enable_wishlist'] ) ? 1 : 0,
		'enable_compare'    => ! empty( $options['enable_compare'] ) ? 1 : 0,
	);
}

/**
 * Render checkbox field.
 *
 * @param array $args Arguments.
 */
function iw_market_checkbox_field( $args ) {
	$options = get_option( 'iw_market_options', array() );
	$value   = ! empty( $options[ $args['label'] ] ) ? 1 : 0;
	?>
	<label>
		<input type="checkbox" name="iw_market_options[<?php echo esc_attr( $args['label'] ); ?>]" value="1" <?php checked( $value, 1 ); ?> />
		<?php echo esc_html__( 'Enabled', 'iw-market' ); ?>
	</label>
	<?php
}

/**
 * Render options page.
 */
function iw_market_render_options_page() {
	?>
	<div class="wrap">
		<h1><?php echo esc_html__( 'IW Market Theme Options', 'iw-market' ); ?></h1>
		<form method="post" action="options.php">
			<?php
			settings_fields( 'iw_market_options' );
			do_settings_sections( 'iw-market-options' );
			submit_button();
			?>
		</form>
	</div>
	<?php
}
