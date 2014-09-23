define([], function(){
    var asciiValueOfA = 'a'.charCodeAt(0);
    var asciiValueOf1 = '1'.charCodeAt(0);

    function isValid(index) {
        return index >= 0 && index < 8;
    }

    var Field = function() {
        var self = this;

        var fromPairOfInts = function(column, row) {
            if(isValid(column) && isValid(row)){
                self.column = column;
                self.row = row;
            }else{
                throw new Error("Field initialization attempt with illegal values (column: " + column + ", row: " + row + ")");
            }
        }

        var fromString = function(columnAndRow) {
            if(typeof columnAndRow === 'string' && columnAndRow.length == 2){
                var column = columnAndRow.toLowerCase().charCodeAt(0) - asciiValueOfA;
                var row = columnAndRow.charCodeAt(1) - asciiValueOf1;
                fromPairOfInts(column, row);
            }else{
                throw new Error("Field initialization attempt with illegal value (columnAndRow:" + columnAndRow + ")");
            }
        }

        if(arguments.length == 1){
            fromString(arguments[0]);
        }else{
            fromPairOfInts(arguments[0], arguments[1])
        }
    };

    Field.prototype.toIndex = function() {
        return 8 * this.row + this.column;
    };

    Field.prototype.addVector = function(vector) {
        return new Field(this.column + vector.x, this.row + vector.y);
    }

    Field.prototype.sameRow = function(anotherField) {
        return this.row === anotherField.row;
    };

    Field.prototype.sameColumn = function(anotherField) {
        return this.column === anotherField.column;
    };

    Field.prototype.toString = function() {
        return String.fromCharCode(asciiValueOfA + this.column) + (this.row + 1).toString(); // row + 1 because we want to dispay row 0 as 1 and so on
    };

    Field.fromIndex = function(index) {
        return new Field(index % 8, Math.floor(index / 8));
    };

    return Field;
});
