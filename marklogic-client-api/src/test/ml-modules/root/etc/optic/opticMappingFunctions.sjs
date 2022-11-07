/*
 * Copyright (c) 2018 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
