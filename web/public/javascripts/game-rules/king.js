define(['piece', 'gameState', 'move', 'chessboardUtils', 'field', 'color', 'underscore'], function(Piece, GameState, Move, ChessboardUtils, Field, Color, _){
    var King = function(color) {
        Piece.call(this, color);
        this.symbol = 'k'; // TODO: we dont really need it, just to make Chessboard.findKingField work
    };

    King.prototype = Object.create(Piece.prototype);

    King.prototype.isStandardMove = function(chessboard, move) {
        var columnDiff = Math.abs(move.to.column - move.from.column);
        var rowDiff = Math.abs(move.to.row - move.from.row);
        return (rowDiff > 0 || columnDiff > 0) && (rowDiff < 2 && columnDiff < 2);
    };

    King.prototype.wouldBeCheckedOnWay = function(chessboard, move) {
        var that = this;
        var fieldsBetween = ChessboardUtils.getFieldsBetween(move.from, move.to);
        return _.find(fieldsBetween, function(field){
            return chessboard.withMove(new Move(move.from, move.to), function(){
                return that.isChecked(chessboard, field);
            });
        }) !== undefined;
    };

    King.prototype.isShortCastling = function(chessboard, move) {
        var expectedRow = this.color === Color.white ? 0 : 7;

        if(move.from.column === 4 && move.to.column === 6 && move.from.row === expectedRow && move.to.row == expectedRow){
            return !chessboard.somethingBetween(move.from, new Field(7, expectedRow)) && !this.wouldBeCheckedOnWay(chessboard, move);
        }
        return false;
    };

    King.prototype.isLongCastling = function(chessboard, move) {
        var expectedRow = this.color === Color.white ? 0 : 7;

        if(move.from.column === 4 && move.to.column === 2 && move.from.row === expectedRow && move.to.row == expectedRow){
            return !chessboard.somethingBetween(move.from, new Field(0, expectedRow)) && !this.wouldBeCheckedOnWay(chessboard, move);
        }
        return false;
    };

    King.prototype.isCastling = function(chessboard, move, gameState) {
        var isShortCastling = false,
            isLongCastling = false;

        if(this.isChecked(chessboard, move.from, gameState)){
            return false;
        }

        if(gameState.forColor(this.color).shortCastlingEnabled){
            isShortCastling = this.isShortCastling(chessboard, move);
        }
        if(gameState.forColor(this.color).longCastlingEnabled){
            isLongCastling = this.isLongCastling(chessboard, move);
        }
        return isShortCastling || isLongCastling;
    };

    King.prototype.isLegalMove = function(chessboard, move, gameState) {
        return this.isStandardMove(chessboard, move) || this.isCastling(chessboard, move, gameState);
    };

    function isKing(piece){
        return 'symbol' in piece && piece.symbol === 'k';
    }

    King.prototype.getCheckingFields = function(chessboard, field, gameState) {
        var enemyPiecesFields = chessboard.getFieldsWithPiecesOfColor(this.color.enemy());
        return _.filter(enemyPiecesFields, function(enemyPieceField){
            if(isKing(chessboard.getPiece(enemyPieceField))){
                return chessboard.getPiece(enemyPieceField).isStandardMove(chessboard, new Move(enemyPieceField, field));
            }else{
                return chessboard.canCheck(new Move(enemyPieceField, field), gameState);
            }
        });
    }

    King.prototype.isChecked = function(chessboard, field, passedGameState){
        var gameState = passedGameState || new GameState();
        var checkingFields = this.getCheckingFields(chessboard, field, gameState);
        return checkingFields.length > 0;
    };

    King.prototype.canEscape = function(chessboard, field, gameState) {
        var Vector = ChessboardUtils.Vector;
        var directions = [new Vector(1, 1), new Vector(1, 0), new Vector(1, -1), new Vector(0, -1), new Vector(-1, -1), new Vector(-1, 0), new Vector(-1, 1), new Vector(0, 1)];

        // TODO: it's a common idiom, it may be worth extracting to some method
        var found = _.find(directions, function(direction){
            var move = Move.fromVector(field, direction);
            return move && chessboard.isLegalMove(move, gameState);
        });
        return found !== undefined;
    };

    King.prototype.canBeShieldedOrBeaten = function(chessboard, kingField, gameState) {
        var checkingFields = this.getCheckingFields(chessboard, kingField, gameState);
        var friendlyPiecesFields = chessboard.getFieldsWithPiecesOfColor(this.color);

        var found = _.find(checkingFields, function(checkingField) {
            return _.find(friendlyPiecesFields, function(friendlyField){
                var destinationFields = ChessboardUtils.getFieldsBetween(checkingField, kingField);
                destinationFields.push(checkingField);
                return _.find(destinationFields, function(destinationField){
                    return chessboard.isLegalMove(new Move(friendlyField, destinationField), gameState);
                });
            });
        });
        return found !== undefined;
    };

    King.prototype.canAvoidBeingChecked = function(chessboard, field, gameState) {
        return this.canEscape(chessboard, field, gameState) || this.canBeShieldedOrBeaten(chessboard, field, gameState);
    };

    King.prototype.isCheckmated = function(chessboard, field, gameState) {
        return this.isChecked(chessboard, field, gameState) && !this.canAvoidBeingChecked(chessboard, field, gameState);
    };

    King.prototype.anyMovePossible = function(chessboard, field, passedGameState) {
        var gameState = passedGameState || new GameState();

        return this.canEscape(chessboard, field, gameState);
    }

    King.prototype.applyMove = function(chessboard, move, gameState) {
        if(this.isShortCastling(chessboard, move)){
            var oldRookField = new Field(7, move.from.row);
            var newRookField = new Field(5, move.from.row);
            chessboard.applyMove(new Move(oldRookField, newRookField), gameState);
        }

        if(this.isLongCastling(chessboard, move)){
            var oldRookField = new Field(0, move.from.row);
            var newRookField = new Field(3, move.from.row);
            chessboard.applyMove(new Move(oldRookField, newRookField), gameState);
        }

        gameState.forColor(this.color).shortCastlingEnabled = false;
        gameState.forColor(this.color).longCastlingEnabled = false;
        return gameState;
    }

    King.prototype.canCheck = function(chessboard, move, gameState) {
        return this.isStandardMove(chessboard, move);
    }

    return King;
});
