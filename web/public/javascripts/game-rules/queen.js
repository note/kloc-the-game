define(['piece', 'chessboardUtils'], function(Piece, ChessboardUtils){
    var Queen = function(color) {
        Piece.call(this, color);
    }

    Queen.prototype = Object.create(Piece.prototype);

    Queen.prototype.isLegalMove = function(chessboard, move, gameState) {
        var isRookLikeMove = (move.from.sameRow(move.to) || move.from.sameColumn(move.to));
        var isBishopLikeMove = ChessboardUtils.sameDiagonal(move.from, move.to);
        return (isRookLikeMove || isBishopLikeMove) && !chessboard.somethingBetween(move.from, move.to);
    }

    return Queen;
});
