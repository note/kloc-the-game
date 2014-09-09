'use strict'

define(['game', 'underscore', 'jquery'],
    function(Game, _, $){

        describe("Rook", function() {
            it("can move vertically", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(Game.Color.black));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("c1")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("c7")))).toBe(true);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("c8")))).toBe(true);
            });

            it("can move horizontally", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(Game.Color.black));

                var someLegalDestinations = ["a2", "b2", "d2", "h2"];
                someLegalDestinations.forEach(function(destination){
                    // TODO: use matchers
                    expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field(destination)))).toBe(true);
                })
            });

            it("can take enemy piece", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("c4"), new Game.Rook(Game.Color.white));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("c4")))).toBe(true);
            });

            it("cannot take same color piece", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("c4"), new Game.Rook(Game.Color.black));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("c4")))).toBe(false);
            });

            it("cannot overleap", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("c4"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("e2"), new Game.Rook(Game.Color.white));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("c5")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("c8")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("f2")))).toBe(false);
            });

            it("cannot move other way than vertically and horizontally", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("c4"), new Game.Rook(Game.Color.white));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("b3")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("b4")))).toBe(false);
            });

            it("can be pinned", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("b8"), new Game.King(Game.Color.black));
                chessboard.setPiece(new Game.Field("c7"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("d6"), new Game.Bishop(Game.Color.white));

                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c7"), new Game.Field("b7")))).toBe(false);
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c7"), new Game.Field("c1")))).toBe(false);
            });

            it("isAnyMovePossible can return true", function() {

            });

            it("isAnyMovePossible can return false", function() {

            });
        });

    }
);