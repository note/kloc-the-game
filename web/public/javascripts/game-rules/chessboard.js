'use strict'

define(['underscore'], function(_){

    var asciiValueOfA = 'a'.charCodeAt(0);
    var asciiValueOf1 = '1'.charCodeAt(0);
    var Field = function() {
        var self = this;

        var fromPairOfInts = function(column, row) {

            var valid = function(index) {
                return index >= 0 && index < 8;
            }

            if(valid(column) && valid(row)){
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

    Field.prototype.toString = function() {
        return String.fromCharCode(asciiValueOfA + this.column) + (this.row + 1).toString; // row + 1 because we want to dispay row 0 as 1 and so on
    }

    var Chessboard = function() {

    };

    Chessboard.prototype.setPiece = function(field, piece) {

    };

    Chessboard.prototype.getPiece = function(field) {

    };

    var Move = function(from, to) {
        if(_.isEqual(from, to)){
            throw new Error("Move constructor's arguments must differ" + from + ", " + to);
        }

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
});
