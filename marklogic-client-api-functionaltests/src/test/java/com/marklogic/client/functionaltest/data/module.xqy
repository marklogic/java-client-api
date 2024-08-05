xquery version "1.0-ml";
(:
  Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
:)



module namespace my = "http://my.test.module";
declare function my:useless()
{
    let $x := (1,2,3,4,5)
    return subsequence($x, 2)
};

