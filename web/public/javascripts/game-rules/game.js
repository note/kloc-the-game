define(['field', 'move', 'chessboard', 'color', 'gameState', 'rook', 'bishop', 'knight', 'queen', 'pawn', 'king'], function(Field, Move, Chessboard, Color, GameState, Rook, Bishop, Knight, Queen, Pawn, King){
    var PieceFactory = {
        fromChar: function(pieceChar){
            switch (pieceChar) {
                case 'r':
                    return new Rook(Color.black);
                case 'n':
                    return new Knight(Color.black);
                case 'b':
                    return new Bishop(Color.black);
                case 'q':
                    return new Queen(Color.black);
                case 'k':
                    return new King(Color.black);
                case 'p':
                    return new Pawn(Color.black);
                case 'R':
                    return new Rook(Color.white);
                case 'N':
                    return new Knight(Color.white);
                case 'B':
                    return new Bishop(Color.white);
                case 'Q':
                    return new Queen(Color.white);
                case 'K':
                    return new King(Color.white);
                case 'P':
                    return new Pawn(Color.white);
                default:
                    return undefined;
            }
        }
    };

    function validateSize(arr){
        var arrSizeOk = arr.length == 8;
        var rowsSizesOk = _.every(arr, function(row){
            return _.isString(row) && row.length === 8;
        });
        return arrSizeOk && rowsSizesOk;
    }

    function loadFromArray(arr) {
        if(!validateSize(arr)){
            throw new Error("Cannot load chessboard from array - invalid array");
        }

        var chessboard = new Chessboard();
        _.each(arr, function(rowString, rowIndex){
            // need to compute realRowIndex because first element of arr refers to the last row (fields marked as a8, b8, c8...)
            var realRowIndex = 7 - rowIndex;

            _.each(rowString, function(pieceChar, columnIndex){
                chessboard.setPiece(new Field(columnIndex, realRowIndex), PieceFactory.fromChar(pieceChar));
            });
        });
        return chessboard;
    }

    function getInitialPosition() {
        var initialPositionInput =
            ['rnbqkbnr',
                'pppppppp',
                'xxxxxxxx',
                'xxxxxxxx',
                'xxxxxxxx',
                'xxxxxxxx',
                'PPPPPPPP',
                'RNBQKBNR'
            ];
        return loadFromArray(initialPositionInput);
    }

    var ChessboardFactory = {
        loadFromArray: loadFromArray,
        getInitialPosition: getInitialPosition
    };

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

    ChessGame.prototype.setResultIfFinished = function(){
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

    // TODO: refactor
    ChessGame.prototype.applyMove = function(move){
        if(isStandardMove(move)){
            this.state = this.chessboard.applyMove(move, this.state);
            if(!this.setResultIfFinished()){
                this.nextMoveColor = this.nextMoveColor.enemy();
            }
            this.pendingDrawProposition = false;
        }else{
            if(move === Move.surrender){
                this.setResult(this.nextMoveColor.enemy());
            }else{
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
