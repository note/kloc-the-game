/**
 * Created by michal on 07/09/14.
 */

console.log("bazinga3");

define(['game', 'jquery'],
    function(Game, $){
        describe("A field", function() {
            it("can be constructed from human-readable notation", function() {
                var field = new Game.Field("a3");
                expect(field.row).toEqual(2);
                expect(field.column).toEqual(0);
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
                chessboard.setPiece(new Game.Field("a1"), new Game.Rook(Game.Color.black))
            });
        });

    }
);
