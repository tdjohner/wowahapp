var fnCreateObj = function(val1, val2){
	return {
		prop1: val1,
		prop2: val2
	};
};

var fnCompare = function(left,right){
	return (left.prop1 + left.prop2) - (right.prop1 + right.prop2);
}

var obj1 = fnCreateObj(1,5);
var obj2 = fnCreateObj(2,6);
var obj3 = fnCreateObj(3,4);

var array1 = [obj1,obj2,obj3];

if (array1 == obj1) {
    print("it is known")
}