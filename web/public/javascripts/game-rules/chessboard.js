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
        return "Field(" + String.fromCharCode(asciiValueOfA + this.column) + (this.row + 1).toString + ")"; // row + 1 because we want to dispay row 0 as 1 and so on
    };

    var Chessboard = function() {
        this.fields = new Array(64)
    };

    Chessboard.prototype.setPiece = function(field, piece) {
        this.fields[field.toIndex()] = piece;
    };

    Chessboard.prototype.getPiece = function(field) {
        return this.fields[field.toIndex()];
    };

    function getFieldsByVector(from, to, vector) {
        var fields = [];
        var currentField = from.addVector(vector);
        while(!_.isEqual(currentField, to)){
            fields.push(currentField);
            currentField = currentField.addVector(vector);
        }
        return fields;
    }

    function verticalVector(from, to) {
        return from.row < to.row ? {x: 0, y: 1} : {x: 0, y: -1};
    }

    function horizontalVector(from, to) {
        return from.column < to.column ? {x: 1, y: 0} : {x: -1, y: 0};
    }

    function sameDiagonal(from, to) {
        return Math.abs(to.row - from.row) === Math.abs(to.column - from.column);
    }

    function diagonalVector(from, to) {
        var vector = {};
        vector.x = from.column < to.column ? 1 : -1;
        vector.y = from.row < to.row ? 1 : -1;
        return vector;
    }

    function getFieldsBetween(from, to) {
        if(from.column === to.column){
            return getFieldsByVector(from, to, verticalVector(from, to));
        }

        if(from.row === to.row){
            return getFieldsByVector(from, to, horizontalVector(from, to));
        }

        if(sameDiagonal(from, to)){
            return getFieldsByVector(from, to, diagonalVector(from, to));
        }

        return new Error("getFieldsBetween called with illegal arguments");
    }

    Chessboard.prototype.somethingBetween = function(from, to) {
        var found = _.find(getFieldsBetween(from, to), function(field) {
            return this.getPiece(field) !== undefined;
        }, this);
        return found !== undefined;
    }

    Chessboard.prototype.isLegalMove = function(move) {
        var fromField = move.from.toIndex();
        var toField = move.to.toIndex();
        if(!this.fields[fromField]){
            return false;
        }

        if(this.fields[toField] && this.fields[fromField].color === this.fields[toField].color){
            return false;
        }

        return this.fields[fromField].isLegalMove(this, move);
    }

    var Move = function(from, to) {
        if(_.isEqual(from, to)){
            throw new Error("Move constructor's arguments must differ" + from + ", " + to);
        }

        this.from = from;
        this.to = to;
    }

    Move.prototype.toString = function() {
        return "Move(" + from + ", " + to + ")";
    }

    var Piece = function(color) {
        this.color = color;
    };

    var Rook = function(color) {
        Piece.call(this, color)
    };

    Rook.prototype.isLegalMove = function(chessboard, move) {
        return (move.from.sameRow(move.to) || move.from.sameColumn(move.to)) && !chessboard.somethingBetween(move.from, move.to);
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
