<?php
/**
 * Main template file.
 *
 * @package IW_Market
 */

get_header();
?>
<div class="iw-content iw-header__inner">
	<?php if ( have_posts() ) : ?>
		<?php
		while ( have_posts() ) :
			the_post();
			?>
			<article <?php post_class( 'iw-card' ); ?>>
				<?php the_title( '<h2>', '</h2>' ); ?>
				<div class="entry-content">
					<?php the_content(); ?>
				</div>
			</article>
		<?php endwhile; ?>
	<?php else : ?>
		<p><?php echo esc_html__( 'No posts found.', 'iw-market' ); ?></p>
	<?php endif; ?>
</div>
<?php
get_footer();
