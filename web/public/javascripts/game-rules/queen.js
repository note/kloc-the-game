define(['piece', 'chessboard'], function(Piece, ChessboardModule){
    var ChessboardUtil = ChessboardModule.ChessboardUtil;

    var Queen = function(color) {
        Piece.call(this, color);
    }

    Queen.prototype.isLegalMove = function(chessboard, move, gameState) {
        var isRookLikeMove = (move.from.sameRow(move.to) || move.from.sameColumn(move.to));
        var isBishopLikeMove = ChessboardUtil.sameDiagonal(move.from, move.to);
        return (isRookLikeMove || isBishopLikeMove) && !chessboard.somethingBetween(move.from, move.to);
    }

    return Queen;
});
