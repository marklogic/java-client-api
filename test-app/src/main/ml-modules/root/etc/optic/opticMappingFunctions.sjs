/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

// Module that helps tests in TestOpticOnLiterals.class for map functionality

function colorIdMapper(row) {
    const result = row;
    switch(result.myColorId) {
        case 1:
            result.myColorId = 'RED ROBIN';
            break;
        case 2:
            result.myColorId = 'BLUE JAY';
            break;
        case 3:
            result.myColorId = 'YELLOW PARROT';
            break;
        case 4:
            result.myColorId = 'BLACK CROW';
            break;
        default:
            result.myColorId = 'NO COLOR';
    }
    return result;
};

function fibReducer(previous, row) {
    const i = Array.isArray(previous) ? previous.length : 0;
    const result = row;
    result.i = i;
    switch(i) {
        case 0:
            result.fib = 0;
            break;
        case 1:
            result.fib = 1;
            break;
        default:
            result.fib = previous[i - 2].fib + previous[i - 1].fib;
            break;
    }
    if (previous === void 0) {
        previous = [result];
    } else {
        previous.push(result);
    }
    return previous;
};

module.exports = {
    colorIdMapper : colorIdMapper,
    fibReducer : fibReducer
}
