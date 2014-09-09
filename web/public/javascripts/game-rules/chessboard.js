'use strict'

define(['underscore'], function(_){

    var ChessboardUtil = {};

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
    ChessboardUtil.sameDiagonal = sameDiagonal;

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

    var Chessboard = function() {
        this.fields = new Array(64)
    };

    Chessboard.prototype.setPiece = function(field, piece) {
        this.fields[field.toIndex()] = piece;
    };

    Chessboard.prototype.getPiece = function(field) {
        return this.fields[field.toIndex()];
    };

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


    return {
        Chessboard: Chessboard,
        ChessboardUtil: ChessboardUtil
    }
});
