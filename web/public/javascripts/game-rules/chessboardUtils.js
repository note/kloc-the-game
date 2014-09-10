'use strict'

define([], function() {
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

    function diagonalVector(from, to) {
        var vector = {};
        vector.x = from.column < to.column ? 1 : -1;
        vector.y = from.row < to.row ? 1 : -1;
        return vector;
    }

    var ChessboardUtils = {};

    ChessboardUtils.sameDiagonal = function(from, to) {
        return Math.abs(to.row - from.row) === Math.abs(to.column - from.column);
    };

    ChessboardUtils.getFieldsBetween = function(from, to) {
        if(from.column === to.column){
            return getFieldsByVector(from, to, verticalVector(from, to));
        }

        if(from.row === to.row){
            return getFieldsByVector(from, to, horizontalVector(from, to));
        }

        if(ChessboardUtils.sameDiagonal(from, to)){
            return getFieldsByVector(from, to, diagonalVector(from, to));
        }

        return [];
    };

    return ChessboardUtils;
});