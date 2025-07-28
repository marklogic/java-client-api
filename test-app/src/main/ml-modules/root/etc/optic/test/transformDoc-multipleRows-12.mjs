'use strict';
const doc = external.doc;
const rows = [
    {"number": 1, "theDoc": doc},
    {"number": 2, "theDoc": doc}
];
const output = Sequence.from(rows);
export default output;
