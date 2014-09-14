'use strict'

define(['game', 'underscore'],
    function(Game, _){
        var white = Game.Color.white;
        var black = Game.Color.black;

        describe("Knight", function() {
            var chessboard;

            beforeEach(function(){
                chessboard= new Game.Chessboard();
            });

            it("can move like knight", function() {
                chessboard.setPiece(new Game.Field("e4"), new Game.Knight(black));

                var legalDestinations = ["f6", "g5", "g3", "f2", "d2", "c3", "c5", "d6"];
                legalDestinations.forEach(function(destination){
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field(destination)))).toBe(true);
                });
            });

            it("cannot move different way", function() {
                chessboard.setPiece(new Game.Field("e4"), new Game.Knight(black));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("f5")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("e8")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("b4")))).toBe(false);
            });

            it("can take enemy piece", function() {
                chessboard.setPiece(new Game.Field("e4"), new Game.Knight(black));
                chessboard.setPiece(new Game.Field("c3"), new Game.Rook(white));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c3")))).toBe(true);
            });

            it("cannot take same color piece", function() {
                chessboard.setPiece(new Game.Field("e4"), new Game.Knight(black));
                chessboard.setPiece(new Game.Field("c3"), new Game.Rook(black));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c3")))).toBe(false);
            });

            it("can overleap", function() {
                chessboard.setPiece(new Game.Field("b1"), new Game.Knight(black));
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(black));
                chessboard.setPiece(new Game.Field("c3"), new Game.Rook(white));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c3")))).toBe(false);
            });

            it("can be pinned", function() {
                chessboard.setPiece(new Game.Field("b8"), new Game.King(black));
                chessboard.setPiece(new Game.Field("b7"), new Game.Knight(black));
                chessboard.setPiece(new Game.Field("b1"), new Game.Rook(white));

                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("b7"), new Game.Field("c5")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c7"), new Game.Field("a5")))).toBe(false);
            });

            it("isAnyMovePossible can return true", function() {
                var knight = new Game.Knight(white);
                chessboard.setPiece(new Game.Field("a8"), knight);
                chessboard.setPiece(new Game.Field("b6"), new Game.Bishop(white));
                expect(knight.anyMovePossible(chessboard, new Game.Field("a8"))).toBe(true);
            });

            it("isAnyMovePossible can return false", function() {
                var knight = new Game.Knight(white);
                chessboard.setPiece(new Game.Field("a8"), knight);
                chessboard.setPiece(new Game.Field("b6"), new Game.Bishop(white));
                chessboard.setPiece(new Game.Field("c7"), new Game.Bishop(white));
                expect(knight.anyMovePossible(chessboard, new Game.Field("a8"))).toBe(false);
            });
        });

    }
);
