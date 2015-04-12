var x = require('foo');

(function() {
    if (true)
        require('bar');

    var x = { test: [ require('zot') ]}
})();

function local(require) {
    require('bogus1');
}

require.require('bogus2');
this.require('bogus3');

function f2() {
    var require = null;
    require('bogus4');
}