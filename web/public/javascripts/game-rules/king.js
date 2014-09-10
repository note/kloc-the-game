define(['piece', 'gameState', 'move', 'chessboardUtils', 'underscore'], function(Piece, GameState, Move, ChessboardUtils, _){
    var King = function(color) {
        Piece.call(this, color);
    };

    King.prototype.isLegalMove = function(chessboard, move, gameState) {
        var columnDiff = Math.abs(move.to.column - move.from.column);
        var rowDiff = Math.abs(move.to.row - move.from.row);
        return (rowDiff > 0 || columnDiff > 0) && (rowDiff < 2 && columnDiff < 2);
    };

    King.prototype.getCheckingFields = function(chessboard, field, gameState) {
        var enemyPiecesFields = chessboard.getFieldsWithPiecesOfColor(this.color.enemy());
        return _.filter(enemyPiecesFields, function(enemyPieceField){
            return chessboard.canCheck(new Move(enemyPieceField, field), gameState);
        });
    }

    King.prototype.isChecked = function(chessboard, field, passedGameState){
        var gameState = passedGameState || new GameState();
        var checkingFields = this.getCheckingFields(chessboard, field, gameState);
        return checkingFields.length > 0;
    };

    King.prototype.canEscape = function(chessboard, field, gameState) {
        //TODO: there is vector used in another place - unify it
        var Vector = function(column, row){
            this.x = column;
            this.y = row;
        };

        var directions = [new Vector(1, 1), new Vector(1, 0), new Vector(1, -1), new Vector(0, -1), new Vector(-1, -1), new Vector(-1, 0), new Vector(-1, 1), new Vector(0, 1)];

        // TODO: it's a common idiom, it may be worth extracting to some method
        var found = _.find(directions, function(direction){
            var move = Move.fromVector(field, direction)
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

//    King.prototype.applyMove = function(chessboard, move, gameState) {
//        return gameState;
//    }

    return King;
});
