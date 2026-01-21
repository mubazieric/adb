<?php
/**
 * Demo importer.
 *
 * @package IW_Market_Core
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

add_action( 'admin_menu', 'iw_market_core_demo_menu' );
add_action( 'admin_post_iw_market_import_demo', 'iw_market_core_import_demo' );

/**
 * Register demo menu.
 */
function iw_market_core_demo_menu() {
	add_submenu_page(
		'themes.php',
		__( 'IW Market Demo Import', 'iw-market-core' ),
		__( 'IW Market Demo', 'iw-market-core' ),
		'manage_options',
		'iw-market-demo',
		'iw_market_core_demo_page'
	);
}

/**
 * Render demo page.
 */
function iw_market_core_demo_page() {
	$status = get_option( 'iw_market_demo_imported', false );
	?>
	<div class="wrap">
		<h1><?php echo esc_html__( 'IW Market Demo Import', 'iw-market-core' ); ?></h1>
		<p><?php echo esc_html__( 'Import demo content for the IW Market theme.', 'iw-market-core' ); ?></p>
		<p><strong><?php echo esc_html__( 'Status:', 'iw-market-core' ); ?></strong> <?php echo esc_html( $status ? __( 'Imported', 'iw-market-core' ) : __( 'Not imported', 'iw-market-core' ) ); ?></p>
		<form method="post" action="<?php echo esc_url( admin_url( 'admin-post.php' ) ); ?>">
			<?php wp_nonce_field( 'iw_market_demo_import', 'iw_market_demo_nonce' ); ?>
			<input type="hidden" name="action" value="iw_market_import_demo" />
			<?php submit_button( __( 'Run Demo Import', 'iw-market-core' ) ); ?>
		</form>
	</div>
	<?php
}

/**
 * Import demo content.
 */
function iw_market_core_import_demo() {
	if ( ! current_user_can( 'manage_options' ) ) {
		wp_die( esc_html__( 'Permission denied.', 'iw-market-core' ) );
	}

	check_admin_referer( 'iw_market_demo_import', 'iw_market_demo_nonce' );

	if ( get_option( 'iw_market_demo_imported', false ) ) {
		wp_safe_redirect( admin_url( 'themes.php?page=iw-market-demo' ) );
		exit;
	}

	$home_id = wp_insert_post(
		array(
			'post_title'   => 'Home',
			'post_type'    => 'page',
			'post_status'  => 'publish',
			'post_content' => '<!-- wp:paragraph {"align":"center"} -->\n<p style="text-align:center">Welcome to IW Market demo home page.</p>\n<!-- /wp:paragraph -->',
		)
	);

	$shop_id = wc_get_page_id( 'shop' );
	if ( $shop_id && ! get_post( $shop_id ) ) {
		$shop_id = wp_insert_post(
			array(
				'post_title'  => 'Shop',
				'post_type'   => 'page',
				'post_status' => 'publish',
			)
		);
		update_option( 'woocommerce_shop_page_id', $shop_id );
	}

	if ( $home_id ) {
		update_option( 'show_on_front', 'page' );
		update_option( 'page_on_front', $home_id );
	}

	if ( class_exists( 'WC_Product' ) ) {
		$product = new WC_Product_Simple();
		$product->set_name( 'Kampala Starter Pack' );
		$product->set_regular_price( '35000' );
		$product->set_description( 'Demo product for IW Market.' );
		$product->save();
	}

	update_option( 'iw_market_demo_imported', true );

	wp_safe_redirect( admin_url( 'themes.php?page=iw-market-demo' ) );
	exit;
}
