// config-overrides.js
const path = require('path');
// eslint-disable-next-line import/no-extraneous-dependencies
const { override, addWebpackAlias, addBabelPlugin } = require('customize-cra');

module.exports = override(
  addWebpackAlias({
    '@components': path.resolve(__dirname, 'src/components'),
    '@features': path.resolve(__dirname, 'src/components/features'),
    '@styles': path.resolve(__dirname, 'src/styles'),
    '@pages': path.resolve(__dirname, 'src/pages'),
    '@routes': path.resolve(__dirname, 'src/routes'),
  }),
  addBabelPlugin('@babel/plugin-proposal-private-property-in-object'),
);
