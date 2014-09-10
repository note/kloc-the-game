'use strict'

define(['chessboardUtils', 'field', 'piece', 'gameState', 'king', 'underscore'], function(ChessboardUtils, Field, PieceModule, GameState, King, _){

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
        var found = _.find(ChessboardUtils.getFieldsBetween(from, to), function(field) {
            return this.getPiece(field) !== undefined;
        }, this);
        return found !== undefined;
    }

    Chessboard.prototype.withMove = function(move, fn) {
        var pieceOnDestination = this.getPiece(move.to);
        this.move(move);

        var result = fn.call(this);

        this.setPiece(move.from, this.getPiece(move.to));
        this.setPiece(move.to, pieceOnDestination);
        return result;
    };

    Chessboard.prototype.findKingField = function(color) {
        var allFields = this.allFields();
        var desiredPiece = new King(color);

        return _.find(allFields, function(field){
            return _.isEqual(this.getPiece(field), desiredPiece);
        }, this);
    };

    Chessboard.prototype.leadsToKingBeingChecked = function(move) {
        var activeColor = this.getPiece(move.from).color;
        return this.withMove(move, function(){
            var kingField = this.findKingField(activeColor);

            // in real game there is always king on chessboard, however in some test there is no king
            // TODO: consider another way of handling these situations
            if(kingField === undefined){
                return false;
            }

            return this.getPiece(kingField).isChecked(this, kingField);
        });
    };

    // TODO: canCheck is not a good name
    Chessboard.prototype.canCheck = function(move, currentGameState) {
        var gameState = currentGameState || new GameState();

        var fromField = move.from.toIndex();
        var toField = move.to.toIndex();
        if(!this.fields[fromField]){
            return false;
        }

        if(this.fields[toField] && this.fields[fromField].color === this.fields[toField].color){
            return false;
        }

        return this.fields[fromField].isLegalMove(this, move, gameState);
    }

    Chessboard.prototype.isLegalMove = function(move, currentGameState) {
        return this.canCheck(move, currentGameState) && !this.leadsToKingBeingChecked(move);
    };

    Chessboard.prototype.move = function(move) {
        var activePiece = this.getPiece(move.from);
        this.setPiece(move.from, undefined);
        this.setPiece(move.to, activePiece);
    };

    // TODO: add comment
    Chessboard.prototype.applyMove = function(move, gameState) {
        var newGameState = gameState.clone();
        var activePiece = this.getPiece(move.from);
        newGameState.forColor(activePiece.color).enpassantProneColumn = null;
        var nextGameState = activePiece.applyMove(this, move, newGameState);
        this.move(move);
        return nextGameState;
    }

    Chessboard.prototype.allFields = function() {
        return _.map(this.fields, function(piece, fieldIndex){
            return Field.fromIndex(fieldIndex);
        });
    }

    Chessboard.prototype.getFieldsWithPiecesOfColor = function(color) {
        var allFields = this.allFields();
        return _.filter(allFields, function(field){
            var piece = this.getPiece(field);
            return piece !== undefined && piece.color === color;
        }, this);
    }

    return Chessboard;
});
