<?php
/**
 * Licensing and updates stubs.
 *
 * @package IW_Market
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

add_action( 'admin_menu', 'iw_market_license_menu' );
add_action( 'admin_post_iw_market_activate_license', 'iw_market_activate_license' );
add_action( 'admin_post_iw_market_deactivate_license', 'iw_market_deactivate_license' );

/**
 * Register license page.
 */
function iw_market_license_menu() {
	add_submenu_page(
		'themes.php',
		__( 'IW Market License', 'iw-market' ),
		__( 'IW Market License', 'iw-market' ),
		'manage_options',
		'iw-market-license',
		'iw_market_render_license_page'
	);
}

/**
 * Render license page.
 */
function iw_market_render_license_page() {
	$license = get_option( 'iw_market_license_key', '' );
	$status  = get_option( 'iw_market_license_status', 'inactive' );
	?>
	<div class="wrap">
		<h1><?php echo esc_html__( 'IW Market License', 'iw-market' ); ?></h1>
		<p><?php echo esc_html__( 'Theme updates require a valid license key.', 'iw-market' ); ?></p>
		<form method="post" action="<?php echo esc_url( admin_url( 'admin-post.php' ) ); ?>">
			<?php wp_nonce_field( 'iw_market_license_action', 'iw_market_license_nonce' ); ?>
			<input type="hidden" name="action" value="iw_market_activate_license" />
			<table class="form-table">
				<tr>
					<th scope="row"><?php echo esc_html__( 'License Key', 'iw-market' ); ?></th>
					<td>
						<input type="text" name="license_key" value="<?php echo esc_attr( $license ); ?>" class="regular-text" />
						<p class="description"><?php echo esc_html__( 'Enter your license key to enable updates.', 'iw-market' ); ?></p>
					</td>
				</tr>
			</table>
			<?php submit_button( __( 'Activate License', 'iw-market' ) ); ?>
		</form>
		<form method="post" action="<?php echo esc_url( admin_url( 'admin-post.php' ) ); ?>">
			<?php wp_nonce_field( 'iw_market_license_action', 'iw_market_license_nonce' ); ?>
			<input type="hidden" name="action" value="iw_market_deactivate_license" />
			<?php submit_button( __( 'Deactivate License', 'iw-market' ), 'secondary' ); ?>
		</form>
		<p><strong><?php echo esc_html__( 'Status:', 'iw-market' ); ?></strong> <?php echo esc_html( ucfirst( $status ) ); ?></p>
	</div>
	<?php
}

/**
 * Activate license via remote check.
 */
function iw_market_activate_license() {
	if ( ! current_user_can( 'manage_options' ) ) {
		wp_die( esc_html__( 'Permission denied.', 'iw-market' ) );
	}

	check_admin_referer( 'iw_market_license_action', 'iw_market_license_nonce' );

	$license = isset( $_POST['license_key'] ) ? sanitize_text_field( wp_unslash( $_POST['license_key'] ) ) : '';

	update_option( 'iw_market_license_key', $license );

	$response = iw_market_remote_license_check( $license );
	$status   = ! empty( $response['valid'] ) ? 'active' : 'inactive';

	update_option( 'iw_market_license_status', $status );
	update_option( 'iw_market_license_checked', time() );

	wp_safe_redirect( admin_url( 'themes.php?page=iw-market-license' ) );
	exit;
}

/**
 * Deactivate license.
 */
function iw_market_deactivate_license() {
	if ( ! current_user_can( 'manage_options' ) ) {
		wp_die( esc_html__( 'Permission denied.', 'iw-market' ) );
	}

	check_admin_referer( 'iw_market_license_action', 'iw_market_license_nonce' );
	update_option( 'iw_market_license_status', 'inactive' );
	update_option( 'iw_market_license_checked', time() );

	wp_safe_redirect( admin_url( 'themes.php?page=iw-market-license' ) );
	exit;
}

/**
 * Remote license check stub.
 *
 * @param string $license License key.
 * @return array
 */
function iw_market_remote_license_check( $license ) {
	$endpoint = 'https://updates.example.com/iw-market/license';
	$payload  = array(
		'body'      => array(
			'license' => $license,
			'site'    => home_url(),
		),
		'timeout'   => 15,
		'user-agent' => 'IW Market License/' . wp_get_theme()->get( 'Version' ),
	);

	$response = wp_remote_post( $endpoint, $payload );

	if ( is_wp_error( $response ) ) {
		return array( 'valid' => false );
	}

	$data = json_decode( wp_remote_retrieve_body( $response ), true );

	return is_array( $data ) ? $data : array( 'valid' => false );
}
