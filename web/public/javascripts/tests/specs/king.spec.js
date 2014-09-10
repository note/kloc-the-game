'use strict'

define(['game', 'underscore'],
    function(Game, _){

        describe("King", function() {
            it("moves all directions by one field", function(){
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.King(Game.Color.white));

                var legalFields = ["f5", "f4", "f3", "e3", "d3", "d4", "d5", "e5"];
                legalFields.forEach(function(field){
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field(field)))).toBe(true);
                });
            });

            it("can not move illegal moves", function(){
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.King(Game.Color.white));
                chessboard.setPiece(new Game.Field("e5"), new Game.Pawn(Game.Color.white));

                var illegalFields = ["e5", "e2", "b4", "c3"];
                illegalFields.forEach(function(field){
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field(field)))).toBe(false);
                });
            });


            it("can take enemy piece", function(){
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.King(Game.Color.white));
                chessboard.setPiece(new Game.Field("e5"), new Game.Pawn(Game.Color.black));

                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("e5")))).toBe(true);
            });

            it("can be checked by rook", function(){
                var chessboard = new Game.Chessboard();
                var king = new Game.King(Game.Color.white);
                chessboard.setPiece(new Game.Field("e4"), king);
                chessboard.setPiece(new Game.Field("e1"), new Game.Rook(Game.Color.black));

                expect(king.isChecked(chessboard, new Game.Field("e4"))).toBe(true);
                expect(king.isChecked(chessboard, new Game.Field("e2"))).toBe(true);
                expect(king.isChecked(chessboard, new Game.Field("d3"))).toBe(false);
            });


            it("be checked by knight", function(){
                var chessboard = new Game.Chessboard();
                var king = new Game.King(Game.Color.white);
                chessboard.setPiece(new Game.Field("e4"), king);
                chessboard.setPiece(new Game.Field("c3"), new Game.Knight(Game.Color.black));

                expect(king.isChecked(chessboard, new Game.Field("e4"))).toBe(true);
                expect(king.isChecked(chessboard, new Game.Field("e2"))).toBe(true);
                expect(king.isChecked(chessboard, new Game.Field("e3"))).toBe(false);
            });


            it("be checked by pinned piece", function(){
                var chessboard = new Game.Chessboard();
                var king = new Game.King(Game.Color.white);
                chessboard.setPiece(new Game.Field("c2"), king);
                chessboard.setPiece(new Game.Field("d6"), new Game.Bishop(Game.Color.white));
                chessboard.setPiece(new Game.Field("c7"), new Game.Rook(Game.Color.black)); // this rook is pinned
                chessboard.setPiece(new Game.Field("b8"), new Game.King(Game.Color.black)); // this rook is pinned

                expect(king.isChecked(chessboard, new Game.Field("c2"))).toBe(true);
            });


            it("not be checked by own knight", function(){
                var chessboard = new Game.Chessboard();
                var king = new Game.King(Game.Color.white);
                chessboard.setPiece(new Game.Field("c2"), king);
                chessboard.setPiece(new Game.Field("b4"), new Game.Knight(Game.Color.white)); // this rook is pinned

                expect(king.isChecked(chessboard, new Game.Field("c2"))).toBe(false);
            });

            it("be checkmated", function(){
                var chessboard = new Game.Chessboard();
                var king = new Game.King(Game.Color.white);
                chessboard.setPiece(new Game.Field("a6"), king);
                chessboard.setPiece(new Game.Field("a1"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("b2"), new Game.Rook(Game.Color.black));

                expect(king.isCheckmated(chessboard, new Game.Field("a6"), new Game.GameState())).toBe(true);
            });

            it("is not checkmated when cas escape", function(){
                var chessboard = new Game.Chessboard();
                var king = new Game.King(Game.Color.white);
                chessboard.setPiece(new Game.Field("a6"), king);
                chessboard.setPiece(new Game.Field("b6"), new Game.Pawn(Game.Color.white));
                chessboard.setPiece(new Game.Field("a1"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("b2"), new Game.Rook(Game.Color.black));

                expect(king.isCheckmated(chessboard, new Game.Field("a6"), new Game.GameState())).toBe(false);
            });

            it("be shielded against checkmate", function(){
                var chessboard = new Game.Chessboard();
                var king = new Game.King(Game.Color.white);
                chessboard.setPiece(new Game.Field("a6"), king);
                chessboard.setPiece(new Game.Field("c6"), new Game.Knight(Game.Color.white));
                chessboard.setPiece(new Game.Field("a1"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("b2"), new Game.Rook(Game.Color.black));

                expect(king.isCheckmated(chessboard, new Game.Field("a6"), new Game.GameState())).toBe(false);
            });

            it("not be checkmated when attacker can be taken", function(){
                var chessboard = new Game.Chessboard();
                var king = new Game.King(Game.Color.white);
                chessboard.setPiece(new Game.Field("a6"), king);
                chessboard.setPiece(new Game.Field("a1"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("b2"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("c2"), new Game.Knight(Game.Color.white)); //can take rook a1

                expect(king.isCheckmated(chessboard, new Game.Field("a6"), new Game.GameState())).toBe(false);
            });

            it("be checkmated 2", function(){
                var chessboard = new Game.Chessboard();
                var king = new Game.King(Game.Color.white);
                chessboard.setPiece(new Game.Field("a8"), king);
                chessboard.setPiece(new Game.Field("a7"), new Game.Bishop(Game.Color.white));
                chessboard.setPiece(new Game.Field("d8"), new Game.Rook(Game.Color.white));
                chessboard.setPiece(new Game.Field("e8"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("b1"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("d5"), new Game.Queen(Game.Color.black));
                chessboard.setPiece(new Game.Field("d4"), new Game.King(Game.Color.black));

                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("d5"), new Game.Field("a8")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("d8"), new Game.Field("d5")))).toBe(false);
                expect(king.isCheckmated(chessboard, new Game.Field("a8"), new Game.GameState())).toBe(true);
            });

            it("can have possible moves", function(){
                var chessboard = new Game.Chessboard();
                var king = new Game.King(Game.Color.white);
                chessboard.setPiece(new Game.Field("a1"), king);
                chessboard.setPiece(new Game.Field("a2"), new Game.Knight(Game.Color.white));
                chessboard.setPiece(new Game.Field("b2"), new Game.Knight(Game.Color.white));

                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("a1"), new Game.Field("b1")))).toBe(true);
                expect(king.anyMovePossible(chessboard, new Game.Field("a1"))).toBe(true);
            });

            it("can have no possible moves", function(){
                var chessboard = new Game.Chessboard();
                var king = new Game.King(Game.Color.white);
                chessboard.setPiece(new Game.Field("a1"), king);
                chessboard.setPiece(new Game.Field("a2"), new Game.Knight(Game.Color.white));
                chessboard.setPiece(new Game.Field("b2"), new Game.Knight(Game.Color.white));
                chessboard.setPiece(new Game.Field("c2"), new Game.Bishop(Game.Color.black));

                expect(king.anyMovePossible(chessboard, new Game.Field("a1"))).toBe(false);
            });

            it("can not be to close to opposite King", function(){
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("c1"), new Game.King(Game.Color.white));
                chessboard.setPiece(new Game.Field("e1"), new Game.King(Game.Color.black));

                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c1"), new Game.Field("d1")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c1"), new Game.Field("d2")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c1"), new Game.Field("c2")))).toBe(true);
            });

    });
});
