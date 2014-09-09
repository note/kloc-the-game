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

            it("be checkmated", function(){});

            it("be shielded against checkmate", function(){});

            it("be shielded against checkmate 2", function(){});

            it("not be checkmated when attacker can be taken", function(){});

            it("be checkmated 2", function(){});

            it("have possible moves", function(){});

            it("have no possible moves", function(){});

            it("not be to close to opposite King", function(){});

    });
});
