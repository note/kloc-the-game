/**
 * Created by michal on 08/09/14.
 */

define(function(){

    var asciiValueOfA = 'a'.charCodeAt(0);
    var asciiValueOf1 = '1'.charCodeAt(0);
    var Field = function() {
        var fromPairOfInts = function(column, row) {
            console.log("bazinga!:" + column + ", " + row);

            var valid = function(index) {
                return index >= 0 && index < 8;
            }

            if(valid(column) && valid(row)){
                console.log("bazinga!?:" + column + ", " + row);
                this.column = column;
                this.row = row;
                return this;
            }

            throw new Error("Field initialization attempt with illegal values (column: " + column + ", row: " + row + ")");
        }

        var fromString = function(columnAndRow) {
            if(typeof columnAndRow === 'string' && columnAndRow.length == 2){
                var column = columnAndRow.toLowerCase().charCodeAt(0) - asciiValueOfA;
                var row = columnAndRow.charCodeAt(1) - asciiValueOf1;
                return fromPairOfInts(column, row);
            }

            throw new Error("Field initialization attempt with illegal value (columnAndRow:" + columnAndRow + ")");
        }

        if(arguments.length == 1){
            return fromString(arguments[0]);
        }else{
            return fromPairOfInts(arguments[0], arguments[1])
        }
    };

    var Chessboard = function() {

    };

    Chessboard.prototype.setPiece = function(field, piece) {

    };

    Chessboard.prototype.getPiece = function(field) {

    };

    var Move = function(from, to) {
        this.from = from;
        this.to = to;
    }

    var Piece = function(color) {
        this.color = color;
    };

    var Rook = function(color) {
        Piece.call(this, color)
    };

    Rook.prototype.isMoveLegal = function(chessboard, move) {

    }

    var Color = Object.freeze({
       white: 0,
       black: 1
    });

    return {
        Field: Field,
        Chessboard: Chessboard,
        Move: Move,
        Rook: Rook,
        Color: Color
    };
})
