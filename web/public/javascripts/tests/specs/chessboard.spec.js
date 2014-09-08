'use strict'

define(['game', 'underscore', 'jquery'],
    function(Game, _, $){
        describe("A field", function() {
            it("can be constructed from human-readable notation", function() {
                var field = new Game.Field("a3");
                expect(field.row).toEqual(2);
                expect(field.column).toEqual(0);
            });

            it("cannot be constructed from ill-formed huamn-readable notation", function() {
                function createConstructor(fieldLiteral) {
                    return function() {
                        new Game.Field(fieldLiteral);
                    }
                }
                expect(createConstructor("a311")).toThrow();
                expect(createConstructor("j3")).toThrow();
                expect(createConstructor("a9")).toThrow();
            });
        });

        describe("Move", function() {
            it("can be constructed", function(){
                var move = new Game.Move(new Game.Field("c2"), new Game.Field("c3"));
                expect(_.isEqual(move.from, new Game.Field("c2"))).toBe(true);
                expect(_.isEqual(move.to, new Game.Field("c3"))).toBe(true);
            });

            it("from and to fields must not be the same", function(){
                var constructIllegalMove = function() {
                    new Game.Move(new Game.Field("c2"), new Game.Field("c2"));
                };

                expect(constructIllegalMove).toThrow();
            });

            it("is incorrect when from field on chessboard is empty", function(){
                var chessboard = new Game.Chessboard();
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("a1"), new Game.Field("a2")))).toBe(false);
            });
        });

        describe("Chessboard", function() {
            it("can be constructed", function() {
                var chessboard = new Game.Chessboard();
                expect("setPiece" in chessboard).toBe(true);
            });

            it("has method setPiece which set piece in given field", function() {
                var chessboard = new Game.Chessboard();
                expect("setPiece" in chessboard).toBe(true)
                expect("getPiece" in chessboard).toBe(true);
                chessboard.setPiece(new Game.Field("a1"), new Game.Rook(Game.Color.black));
                expect(_.isEqual(chessboard.getPiece(new Game.Field("a1")), new Game.Rook(Game.Color.black))).toBe(true);
            });
        });

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

            it("cannot overleap", function() {
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("c2"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("c4"), new Game.Rook(Game.Color.black));
                chessboard.setPiece(new Game.Field("e2"), new Game.Rook(Game.Color.white));

                // TODO: use matchers
                expect(chessboard.isLegalMove(new Game.Move(new Game.Field("c2"), new Game.Field("c4")))).toBe(false);
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

            });

            it("isAnyMovePossible can return true", function() {

            });

            it("isAnyMovePossible can return false", function() {

            });
        });
    }
);
