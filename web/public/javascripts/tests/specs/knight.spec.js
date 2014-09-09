'use strict'

define(['game', 'underscore', 'jquery'],
    function(Game, _, $){

        describe("Knight", function() {
            it("can move like knight", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Knight(Game.Color.black));

                var legalDestinations = ["f6", "g5", "g3", "f2", "d2", "c3", "c5", "d6"];
                legalDestinations.forEach(function(destination){
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field(destination)))).toBe(true);
                });
            });

            it("cannot move different way", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Knight(Game.Color.black));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("f5")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("e8")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("b4")))).toBe(false);
            });

            it("can take enemy piece", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Knight(Game.Color.black));
                chessboard.setPiece(new Game.Field("c3"), new Game.Rook(Game.Color.white));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c3")))).toBe(true);
            });

            it("cannot take same color piece", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Knight(Game.Color.black));
                chessboard.setPiece(new Game.Field("c3"), new Game.Rook(Game.Color.black));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c3")))).toBe(false);
            });

            it("can overleap", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("b1"), new Game.Knight(Game.Color.black));
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("c3"), new Game.Rook(Game.Color.white));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("e4"), new Game.Field("c3")))).toBe(false);
            });

            it("can be pinned", function() {

            });

            it("isAnyMovePossible can return true", function() {

            });

            it("isAnyMovePossible can return false", function() {

            });
        });

    }
);