define(['piece'], function(Piece){
    var King = function(color) {
        Piece.call(this, color);
    }

    King.prototype.isLegalMove = function(chessboard, move) {
        return false;
    }

    return King;
});
