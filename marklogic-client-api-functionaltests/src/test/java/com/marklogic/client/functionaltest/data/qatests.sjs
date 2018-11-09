function Mymin(a, b) {
  var inputInt1 = parseInt(a, 10);
  var inputInt2 = parseInt(b, 10);
  var arr = [inputInt1, inputInt2];
  var min = fn.min(arr);
  return new NodeBuilder().addNumber(min).toNode();
};
exports.Mymin = Mymin;
