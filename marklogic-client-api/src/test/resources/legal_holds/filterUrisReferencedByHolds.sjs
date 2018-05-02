// var uris is an array provided at module invocation
var uris;

// var urisWithoutHolds we will popoulate and return
var urisWithoutHolds = [];

// for each provided uri, check to make sure no hold doc references it
uris.map(
    function(uri) {
        if ( ! cts.exists(cts.andQuery(
                 [ cts.jsonPropertyValueQuery('hold', 'true'),
                   cts.jsonPropertyValueQuery('referencedUris', uri) ])) )
        {
            // no hold doc references it
            urisWithoutHolds.push(uri)
        }
    }
);

// return only the uris not referenced by hold docs (if any)
urisWithoutHolds.join(",");
