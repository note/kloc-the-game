'use strict'

define(['game', 'underscore', 'jquery'],
    function(Game, _, $){

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
                expect(chessboard.getPiece(new Game.Field("a1"))).toEqual(new Game.Rook(Game.Color.black));
            });
        });

        describe("ChessboardFactory", function() {
            it("can create chessboard from array", function(){
                var input =
                    ["rxxxxxxx",
                     "xxxxxxxk",
                     "xxxxxxxx",
                     "xxxxxxxx",
                     "rxxxxxxx",
                     "xxxxxxxx",
                     "xxxxxxxB",
                     "xKxxxxxx"
                    ];

                var chessboard = Game.ChessboardFactory.loadFromArray(input);
                expect("getPiece" in chessboard).toBe(true);
                expect(chessboard.getPiece(new Game.Field("a8"))).toEqual(new Game.Rook(Game.Color.black));
                expect(chessboard.getPiece(new Game.Field("h7"))).toEqual(new Game.King(Game.Color.black));
                expect(chessboard.getPiece(new Game.Field("h2"))).toEqual(new Game.Bishop(Game.Color.white));
                expect(chessboard.getPiece(new Game.Field("b1"))).toEqual(new Game.King(Game.Color.white));

                expect(chessboard.getPiece(new Game.Field("b8"))).toBeUndefined();
                expect(chessboard.getPiece(new Game.Field("c8"))).toBeUndefined();
            });

            it("throws an exception when illegal array passed to loadFromArray", function(){
                var incorrectInput1 =
                    ["rxxxxxxx",
                     "xxxxxxxk",
                     "xxxxxxxx",
                     "xxxxxxxx",
                     "rxxxxxxx",
                     "xxxxxxxx",
                     "xxxxxxxB"
                    ];
                var incorrectInput2 =
                    ["rxxxxxxxx",
                     "xxxxxxxk",
                     "xxxxxxxx",
                     "xxxxxxxx",
                     "rxxxxxxx",
                     "xxxxxxxx",
                     "xxxxxxxB",
                     "xKxxxxxx"
                    ];

                var createLoadFun = function(input){
                    return function(){
                        Game.ChessboardFactory.loadFromArray(input);
                    }
                };

                expect(createLoadFun(incorrectInput1)).toThrow();
                expect(createLoadFun(incorrectInput2)).toThrow();
            })
        });

        describe("Method Chessboard.somethingBetween", function() {
            it("returns false if there is no piece between given two fields", function(){
                var chessboard = new Game.Chessboard();
                expect(chessboard.somethingBetween(new Game.Field("e1"), new Game.Field("e5"))).toBe(false);
                expect(chessboard.somethingBetween(new Game.Field("f7"), new Game.Field("f1"))).toBe(false);
                expect(chessboard.somethingBetween(new Game.Field("b2"), new Game.Field("h2"))).toBe(false);
                expect(chessboard.somethingBetween(new Game.Field("g3"), new Game.Field("f3"))).toBe(false);

                chessboard.setPiece(new Game.Field("e4"), new Game.Rook(Game.Color.white));
                expect(chessboard.somethingBetween(new Game.Field("e1"), new Game.Field("e4"))).toBe(false);
                expect(chessboard.somethingBetween(new Game.Field("e4"), new Game.Field("e8"))).toBe(false);
            });

            it("returns true if there is a piece between given to fields", function(){
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Rook(Game.Color.white));
                expect(chessboard.somethingBetween(new Game.Field("e1"), new Game.Field("e5"))).toBe(true);
                expect(chessboard.somethingBetween(new Game.Field("e6"), new Game.Field("e3"))).toBe(true);
                expect(chessboard.somethingBetween(new Game.Field("d4"), new Game.Field("f4"))).toBe(true);
                expect(chessboard.somethingBetween(new Game.Field("f4"), new Game.Field("a4"))).toBe(true);
            });

            it("works for diagonals too", function(){
                var chessboard = new Game.Chessboard();
                chessboard.setPiece(new Game.Field("e4"), new Game.Rook(Game.Color.white));
                expect(chessboard.somethingBetween(new Game.Field("d3"), new Game.Field("f5"))).toBe(true);
                expect(chessboard.somethingBetween(new Game.Field("f5"), new Game.Field("d3"))).toBe(true);
                expect(chessboard.somethingBetween(new Game.Field("d5"), new Game.Field("f3"))).toBe(true);
                expect(chessboard.somethingBetween(new Game.Field("f3"), new Game.Field("d5"))).toBe(true);

                expect(chessboard.somethingBetween(new Game.Field("a3"), new Game.Field("c5"))).toBe(false);
                expect(chessboard.somethingBetween(new Game.Field("c5"), new Game.Field("a3"))).toBe(false);
                expect(chessboard.somethingBetween(new Game.Field("a5"), new Game.Field("c3"))).toBe(false);
                expect(chessboard.somethingBetween(new Game.Field("c3"), new Game.Field("a5"))).toBe(false);
            });

            it("throws an exception when given to fields are not neither on same column nor row nor diagonal", function(){
                var chessboard = new Game.Chessboard();

                var callSomethingBetween = function() {
                    chessboard.somethingBetween(new Game.Field("e2"), new Game.Field("d4"));
                }
            });
        })

    }
);
