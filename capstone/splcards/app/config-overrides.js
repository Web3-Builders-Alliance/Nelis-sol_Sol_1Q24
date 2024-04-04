const webpack = require('webpack');

module.exports = function override(config, env) {
    // Setup fallbacks for Node.js modules
    config.resolve.fallback = {
        ...(config.resolve.fallback || {}),
        "crypto": require.resolve("crypto-browserify"),
        "stream": require.resolve("stream-browserify"),
        "buffer": require.resolve('buffer/'),
    };

    config.plugins = (config.plugins || []).concat([
        new webpack.ProvidePlugin({
            Buffer: ['buffer', 'Buffer'],
        }),
    ]);

    // Return the overridden config
    return config;
};
