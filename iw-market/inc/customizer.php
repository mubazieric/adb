<?php
/**
 * Theme Customizer settings.
 *
 * @package IW_Market
 */

if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

add_action( 'customize_register', 'iw_market_customize_register' );

/**
 * Register Customizer settings.
 *
 * @param WP_Customize_Manager $wp_customize Customizer manager.
 */
function iw_market_customize_register( $wp_customize ) {
	$wp_customize->add_section(
		'iw_market_header',
		array(
			'title'    => __( 'Header Layout', 'iw-market' ),
			'priority' => 30,
		)
	);

	$wp_customize->add_setting(
		'iw_market_header_layout',
		array(
			'default'           => 'classic',
			'sanitize_callback' => 'sanitize_text_field',
		)
	);

	$wp_customize->add_control(
		'iw_market_header_layout',
		array(
			'label'   => __( 'Header Layout', 'iw-market' ),
			'section' => 'iw_market_header',
			'type'    => 'select',
			'choices' => array(
				'classic'  => __( 'Classic', 'iw-market' ),
				'centered' => __( 'Centered Logo', 'iw-market' ),
				'minimal'  => __( 'Minimal + Off-canvas', 'iw-market' ),
			),
		)
	);

	$wp_customize->add_setting(
		'iw_market_sticky_header',
		array(
			'default'           => true,
			'sanitize_callback' => 'rest_sanitize_boolean',
		)
	);

	$wp_customize->add_control(
		'iw_market_sticky_header',
		array(
			'label'   => __( 'Enable Sticky Header', 'iw-market' ),
			'section' => 'iw_market_header',
			'type'    => 'checkbox',
		)
	);
}
