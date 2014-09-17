'use strict'

define(['chessboardUtils', 'field', 'piece', 'gameState', 'king', 'underscore'], function(ChessboardUtils, Field, PieceModule, GameState, King, _){

    /* private methods */

    function leadsToKingBeingChecked(move) {
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
    }

    function getAllFields() {
        return _.map(this.fields, function(piece, fieldIndex){
            return Field.fromIndex(fieldIndex);
        });
    }

    /* public methods */

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
        var allFields = getAllFields.call(this);
        var desiredPiece = new King(color);

        return _.find(allFields, function(field){
            return _.isEqual(this.getPiece(field), desiredPiece);
        }, this);
    };

    /**
     * This methods checks if given move is legal but ignores if given move leads to king being checked. So this method
     * is useful if we want to check if a piece checks enemy king. The crucial observation is that piece checks king
     * even if it (attacking piece) is pinned. Besides of checking if king is under check only method Chessboard.isLegalMove
     * should be used.
     *
     * @param move
     * @param currentGameState
     * @returns {*}
     */
    Chessboard.prototype.canMove = function(move, currentGameState) {
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

    /**
     * Returns true if given move for given game state is legal, returns false otherwise.
     *
     * @param move
     * @param currentGameState
     * @returns true if given move for given game state is legal, returns false otherwise
     */
    Chessboard.prototype.isLegalMove = function(move, currentGameState) {
        return this.canMove(move, currentGameState) && !leadsToKingBeingChecked.call(this, move);
    };

    Chessboard.prototype.move = function(move) {
        var activePiece = this.getPiece(move.from);
        this.setPiece(move.from, undefined);
        this.setPiece(move.to, activePiece);
    };

    /**
     * This method apply given move for chessboard object. This method does NOT check if given move is correct.
     *
     * By applying move we mean getting new game state and mutating chessboard according to move.
     *
     * @param move
     * @param gameState
     * @returns new game state
     */
    Chessboard.prototype.applyMove = function(move, gameState) {
        function getNextState() {
            var newGameState = gameState.clone();
            var activePiece = this.getPiece(move.from);
            newGameState.forColor(activePiece.color).enpassantProneColumn = null;
            var nextGameState = activePiece.applyMove(this, move, newGameState);
            return nextGameState;
        }
        var nextGameState = getNextState.call(this);
        this.move(move);
        return nextGameState;
    }

    Chessboard.prototype.getFieldsWithPiecesOfColor = function(color) {
        var allFields = getAllFields.call(this);
        return _.filter(allFields, function(field){
            var piece = this.getPiece(field);
            return piece !== undefined && piece.color === color;
        }, this);
    }

    return Chessboard;
});
