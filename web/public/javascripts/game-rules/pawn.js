define(['piece', 'color', 'field', 'chessboardUtils'], function(Piece, Color, Field, ChessboardUtils){
    var Pawn = function(color) {
        Piece.call(this, color);
        this.symbol = 'p';
    };

    Pawn.prototype = Object.create(Piece.prototype);

    Pawn.prototype.fromStartPosition = function(from) {
        if(this.color === Color.white){
            return from.row === 1;
        }else{
            return from.row === 6;
        }
    };

    Pawn.prototype.legalDiffsForGoingForward = function(from) {
        var diffs = this.fromStartPosition(from) ? [1, 2] : [1];
        if(this.color === Color.black){
            diffs = _.map(diffs, function(diff){
                return -diff;
            });
        }
        return diffs;
    };

    Pawn.prototype.legalDiffForTaking = function() {
        return this.color === Color.white ? 1 : -1;
    }

    Pawn.prototype.isGoingForward = function(chessboard, move) {
        if(move.from.sameColumn(move.to) && chessboard.getPiece(move.to) === undefined){
            var diff = move.to.row - move.from.row;
            var legalDiffs = this.legalDiffsForGoingForward(move.from);
            var somethingBetween = chessboard.somethingBetween(move.from, move.to);
            return _.contains(legalDiffs, diff) && !somethingBetween;
        }
        return false;
    };

    Pawn.prototype.isTakingEnemy = function(move, chessboard) {
        var pieceOnDestination = chessboard.getPiece(move.to);
        var enemyOnDestinationField = pieceOnDestination !== undefined && pieceOnDestination.color !== this.color;
        if(enemyOnDestinationField){
            var nextColumn = Math.abs(move.to.column - move.from.column) === 1;
            var diff = move.to.row - move.from.row;
            return nextColumn && diff === this.legalDiffForTaking();
        }
        return false;
    };

    Pawn.prototype.isLegalRowForEnpassant = function(from) {
        if(this.color === Color.white){
            return from.row === 4;
        }else{
            return from.row === 3;
        }
    };

    Pawn.prototype.isEnpassant = function(chessboard, move, gameState) {
        var nextColumn = Math.abs(move.to.column - move.from.column) === 1;
        var isLegalDiff = move.to.row - move.from.row === this.legalDiffForTaking();
        var isLegalRow = this.isLegalRowForEnpassant(move.from);
        if(nextColumn && isLegalDiff && isLegalRow){
            return move.to.column === gameState.forColor(this.color.enemy()).enpassantProneColumn;
        }
        return false;
    };

    Pawn.prototype.isCorrectPromotion = function(move){
        var newPiece = move.promoteTo;
        var sameColor = this.color === newPiece.color;
        var correctPiece = newPiece.symbol !== 'k' && newPiece.symbol !== 'p';
        return sameColor && correctPiece;
    }

    Pawn.prototype.isLegalMove = function(chessboard, move, gameState) {
        if('promoteTo' in move){
            return (this.isGoingForward(chessboard, move) || this.isTakingEnemy(move, chessboard)) && this.isCorrectPromotion(move);
        }else{
            return this.isGoingForward(chessboard, move) || this.isTakingEnemy(move, chessboard) || this.isEnpassant(chessboard, move, gameState);
        }
    };

    // TODO: add comment
    Pawn.prototype.applyMove = function(chessboard, move, gameState) {
        if('promoteTo' in move){
            chessboard.setPiece(move.from, move.promoteTo);
        }

        if(this.isEnpassant(chessboard, move, gameState)){
            var beatenPawnField = new Field(move.to.column, move.from.row);
            chessboard.setPiece(beatenPawnField, undefined);
        }

        if(Math.abs(move.to.row-move.from.row) === 2){
            gameState.forColor(chessboard.getPiece(move.from).color).enpassantProneColumn = move.from.column;
        }
        return gameState;
    };

    Pawn.prototype.vectors = function() {
        var Vector = ChessboardUtils.Vector;
        if(this.color === Color.white){
            return [new Vector(-1, 1), new Vector(0, 1), new Vector(1, 1)];
        }else{
            return [new Vector(-1, -1), new Vector(0, -1), new Vector(1, -1)];
        }
    }

    return Pawn;
});
