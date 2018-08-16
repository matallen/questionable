var app = angular.module('myApp', []);
app.directive('percentage', function () {
return {
    require: 'ngModel',
    link: function (scope, element, attrs, modelCtrl) {
        scope.$watch(attrs.ngModel, function (newValue, oldValue) {
            //debugger;
            if (newValue!=undefined){
              newValue = newValue.replace('%','');
              var cursor=element[0].selectionEnd>newValue.length?newValue.length:element[0].selectionEnd;
              
              if (!isNaN(parseFloat(newValue)) && isFinite(newValue)){
                newValue=newValue+attrs.placeholder;
              }else{
                return;
              }
              var varName=modelCtrl.$name;
              scope.models[varName]=newValue;
              element[0].selectionEnd=cursor;
            }
        });
    }
   };
});
