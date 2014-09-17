define(['field', 'move', 'chessboard', 'color', 'gameState', 'rook', 'bishop', 'knight', 'queen', 'pawn', 'king', 'chessboardFactory'], function(Field, Move, Chessboard, Color, GameState, Rook, Bishop, Knight, Queen, Pawn, King, ChessboardFactory){

    function setResultIfFinished(){
        var checkmate = this.isCheckmate();
        if(checkmate){
            this.setResult(this.nextMoveColor);
            return true;
        }

        var staleMate = this.isStalemate();
        if(staleMate){
            this.setResult(ChessGame.Result.draw);
            return true;
        }

        return false;
    }

    function isDrawPropositionRelated(move){
        return 'drawPropositionRelated' in move && move.drawPropositionRelated;
    }

    function isStandardMove(move){
        return !isDrawPropositionRelated(move) && move !== Move.surrender;
    }

    function handleStandardMove(move) {
        this.state = this.chessboard.applyMove(move, this.state);
        if(!setResultIfFinished.call(this)){
            this.nextMoveColor = this.nextMoveColor.enemy();
        }
        this.pendingDrawProposition = false;
    }

    function handleDrawProposition(move) {
        switch(move){
            case Move.proposeDraw:
                this.pendingDrawProposition = true;
                break;
            case Move.propositionAccepted:
                if(this.pendingDrawProposition){
                    this.setResult(ChessGame.Result.draw);
                }
                break;
            case Move.propositionRejected:
                this.pendingDrawProposition = false;
                break;
        }
    }

    var ChessGame = function(chessboard, nextMoveColor){
        this.chessboard = chessboard || ChessboardFactory.getInitialPosition();
        this.state = new GameState();
        this.nextMoveColor = nextMoveColor || Color.white;
        this.result = null;
    };

    ChessGame.prototype.isLegalMove = function(move){
        var activePiece = this.chessboard.getPiece(move.from);
        if(activePiece.color === this.nextMoveColor){
            return this.chessboard.isLegalMove(move, this.state);
        }
        return false;
    };

    ChessGame.prototype.setResult = function(result){
        this.result = result;
        this.nextMoveColor = undefined;
    }

    ChessGame.prototype.isCheckmate = function(){
        var kingField = this.chessboard.findKingField(this.nextMoveColor.enemy());
        if(!kingField){
            throw new Error("Chessboard without King");
        }
        return this.chessboard.getPiece(kingField).isCheckmated(this.chessboard, kingField, this.gameState);
    }

    ChessGame.prototype.isStalemate = function(){
        var color = this.nextMoveColor.enemy();
        var fields = this.chessboard.getFieldsWithPiecesOfColor(color);
        return _.find(fields, function(field){
            return this.chessboard.getPiece(field).anyMovePossible(this.chessboard, field, this.gameState);
        }, this) === undefined;
    }

    ChessGame.prototype.applyMove = function(move){
        if(isStandardMove(move)){
            handleStandardMove.call(this, move);
        }else{
            if(move === Move.surrender){
                this.setResult(this.nextMoveColor.enemy());
            }else{
                handleDrawProposition.call(this, move);
            }
        }
    };

    var draw = {};

    ChessGame.Result = Object.freeze({
        whiteWinner: Color.white,
        blackWinner: Color.black,
        draw: draw
    });

    return {
        Field: Field,
        Move: Move,
        Chessboard: Chessboard,
        ChessboardFactory: ChessboardFactory,
        Color: Color,
        GameState: GameState,
        Rook: Rook,
        Bishop: Bishop,
        Knight: Knight,
        Queen: Queen,
        Pawn: Pawn,
        King: King,
        ChessGame: ChessGame
    };
});
