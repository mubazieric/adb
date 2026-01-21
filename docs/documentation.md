# IW Market Documentation

## Install Steps
1. Upload `iw-market` to `/wp-content/themes/`.
2. Upload `iw-market-child` to `/wp-content/themes/` if you want to customize safely.
3. Activate IW Market (or child theme) in Appearance → Themes.
4. Install and activate WooCommerce.
5. Install and activate the companion plugin `iw-market-core`.

## Demo Import Steps
1. Go to Appearance → IW Market Demo.
2. Click “Run Demo Import”.
3. Visit the homepage to confirm the demo content is loaded.

## Customization Guide
- Header layout: Appearance → Customize → Header Layout.
- Theme options: Appearance → IW Market Options.
- License: Appearance → IW Market License.

## Developer Hooks & Filters
- `iw_market_setup` (action): Theme setup.
- `iw_market_enqueue_assets` (action): Enqueue assets.
- `iw_market_ajax_search` (action): AJAX search endpoint.

## Local Testing Guide
1. Install WordPress + WooCommerce on PHP 8.0+.
2. Activate IW Market and IW Market Core.
3. Import demo content.
4. Verify AJAX search: open the search modal and type.
5. Verify cart drawer link and mobile bottom nav links.

## Automated Checks
- Run PHPCS with WordPress ruleset (if installed): `phpcs`.
- Run PHPUnit for helper functions if tests are added.
