define(['piece'], function(Piece){
    var Rook = function(color) {
        Piece.call(this, color);
    };

    Rook.prototype.isLegalMove = function(chessboard, move) {
        return (move.from.sameRow(move.to) || move.from.sameColumn(move.to)) && !chessboard.somethingBetween(move.from, move.to);
    }

    return Rook;
});
