"use strict"

require(['jquery', 'cookie', 'game', 'underscore'], function($, __notUsed, Game, _) {
    var registerUserUrl = '@routes.Application.registerUser()';
    var createRoomUrl = '@routes.Application.createRoom()';
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket

    var white = Game.Color.white;
    var black = Game.Color.black;
    var King = Game.King;
    var Queen = Game.Queen;
    var Rook = Game.Rook;
    var Bishop = Game.Bishop;
    var Knight = Game.Knight;
    var Pawn = Game.Pawn;

    function createGameWebsocket(url){
        var socket = new WS(url);

        var receiveMessage = function(msg){
            console.log(msg);
            console.log(msg.data);
        };

        socket.onmessage = receiveMessage;
        socket.onclose = function(){
            console.log("websocket closed");
        }
        socket.onerror = function(){
            console.log("websocket onerror");
        }
        return socket;
    }

    $(document).ready(function() {
        console.log("ready!");


        var canvas = $("#chessboard-canvas").get(0);
        var canvasCtx = canvas.getContext("2d");
        var img = $("#pieces-image").get(0);
//        renderer.drawPieceImage(renderer.getPieceImageFn(new Pawn(black)), new Game.Field("c6"));

        var chessboardCanvas = $("#chessboard-canvas").get(0);
        var chessboardContext = chessboardCanvas.getContext('2d');
        var fieldSize = 100;
        var playerColor = white;
        for(var row = 0; row < 8; ++row){
            for(var column = 0; column < 8; ++column){
                chessboardContext.beginPath();
                chessboardContext.rect(column * fieldSize, row * fieldSize, fieldSize, fieldSize);
                chessboardContext.fillStyle = column % 2 == row % 2 ? '#fff' : '#a1a1a1';
                chessboardContext.fill();
            }
        }

        function drawPiece(piece, field){
            var srcForPiece = piece.attr("src");
            var marginTop = Math.floor((fieldSize - piece.height())/2);
            var top = playerColor === white ? 7 - field.row : field.row;
            var topPos = top * fieldSize + marginTop;
            var marginLeft = Math.floor((fieldSize - piece.width())/2);
            var left = playerColor === white ? field.column : 7 - field.column;
            var leftPos = left * fieldSize + marginLeft;
            var html = '<div style="width: ' + fieldSize + 'px; height: ' + fieldSize + '; top:' + topPos + 'px; left: ' + leftPos + 'px; position: absolute;"><img src="' + srcForPiece + '"</div>';
            $("#pieces-layer").append(html);
        }

        drawPiece($("#black-king"), new Game.Field("b7"));
        drawPiece($("#black-queen"), new Game.Field("c7"));
        drawPiece($("#black-rook"), new Game.Field("d7"));
        drawPiece($("#black-bishop"), new Game.Field("e7"));
        drawPiece($("#black-knight"), new Game.Field("f7"));
        drawPiece($("#black-pawn"), new Game.Field("g7"));

        drawPiece($("#white-king"), new Game.Field("b6"));
        drawPiece($("#white-queen"), new Game.Field("c6"));
        drawPiece($("#white-rook"), new Game.Field("d6"));
        drawPiece($("#white-bishop"), new Game.Field("e6"));
        drawPiece($("#white-knight"), new Game.Field("f6"));
        drawPiece($("#white-pawn"), new Game.Field("g6"));

//        console.log("here12");
//        var game = new Game.ChessGame()
//        var allFields = game.chessboard.getAllFields()
//        _.each(allFields, function(field){
//            var piece = game.chessboard.getPiece(field);
//            if(piece !== undefined)
////                renderer.drawPieceImage(renderer.getPieceImageFn(piece), field);
//        });
//
//        canvasCtx.drawImage(img, 0, 0, pieceWidth, pieceHeight, 0, 0, pieceWidth, pieceHeight);

//        $("#send-introduce").click(function(){
//            var playerName = $("#player-name").val();
//            $.get(registerUserUrl, {"playerName": playerName}, function(data){
//                console.log(data);
//                $.cookie("userId", data.userId);
//            });
//        });
//
//        $('#create-room').click(function(){
//            var roomName = $('#new-room-name').val();
//            $.get(createRoomUrl, {name: roomName}, function(data){
//                console.log(data);
////                var wsUrl = data.url;
////                createGameWebsocket(wsUrl);
//            });
//        });
//
//        $(".join-room").click(function(){
//            var that = this;
//
//            function createUrl(baseUrl) {
//                var params = {
//                    color: $(that).hasClass("white") ? "w" : "b"
//                };
//                return [baseUrl, "&", $.param(params)].join("");
//            }
//
//            var url = createUrl($(this).attr("href"));
//            var socket = createGameWebsocket(url);
//            $("#game-panel").show();
//            $("#send-move").click(function(){
//                var fromStr = $("#from").val();
//                var toStr = $("#to").val();
//                var moveMessage = {type: "move", from: fromStr, to: toStr};
//                socket.send(JSON.stringify(moveMessage));
//            });
//        });
    });

});
