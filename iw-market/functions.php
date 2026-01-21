<?php
/**
 * IW Market theme bootstrap.
 *
 * @package IW_Market
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

require_once get_template_directory() . '/inc/setup.php';
require_once get_template_directory() . '/inc/assets.php';
require_once get_template_directory() . '/inc/customizer.php';
require_once get_template_directory() . '/inc/options/panel.php';
require_once get_template_directory() . '/inc/woocommerce/hooks.php';
require_once get_template_directory() . '/inc/ajax/search.php';
require_once get_template_directory() . '/inc/helpers/licenses.php';
require_once get_template_directory() . '/inc/helpers/template-tags.php';
