xquery version "1.0-ml";

module namespace my = "http://my.test.module";
declare function my:useless()
{
    let $x := (1,2,3,4,5)
    return subsequence($x, 2)
};

