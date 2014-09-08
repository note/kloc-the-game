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
        });

//        describe("Chessboard", function() {
//            it("can be constructed", function() {
//                var chessboard = new Game.Chessboard();
//                expect("setPiece" in chessboard).toBe(true);
//            });
//
//            it("has method setPiece which set piece in given field", function() {
//                var chessboard = new Game.Chessboard();
//                expect("setPiece" in chessboard).toBe(true)
//                expect("getPiece" in chessboard).toBe(true);
//                chessboard.setPiece(new Game.Field("a1"), new Game.Rook(Game.Color.black));
//                expect(_.isEqual(chessboard.getPiece("a1"), new Game.Rook(Game.Color.black))).toBe(true);
//            });
//        });
//
//        describe("Rook", function() {
//            it("can move vertically", function() {
//
//            });
//
//            it("can move horizontally", function() {
//
//            });
//
//            it("can take enemy piece", function() {
//
//            });
//
//            it("cannot move other way than vertically and horizontally", function() {
//
//            });
//
//
//            it("cannot overleap", function() {
//
//            });
//
//            it("can be pinned", function() {
//
//            });
//
//            it("isAnyMovePossible can return true", function() {
//
//            });
//
//            it("isAnyMovePossible can return false", function() {
//
//            });
//        });
    }
);
