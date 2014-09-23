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

        function drag(event){
            console.log(event.originalEvent.target.id);
            event.originalEvent.dataTransfer.setData("from", event.originalEvent.target.id);
        }

        function drop(event){
            console.log("ondrop");
            console.log(event.originalEvent.dataTransfer.getData("from"));
            console.log(event.target.id);
        }

        function dragover(event){
            console.log("ondragover");
            var dt = event.originalEvent.dataTransfer;
            dt.dropEffect = "move";
            event.preventDefault();
        }

        function drawPiece(piece, field){
            var pieces = {
                k: '#black-king',
                q: '#black-queen',
                r: '#black-rook',
                b: '#black-bishop',
                n: '#black-knight',
                p: '#black-pawn',

                K: '#white-king',
                Q: '#white-queen',
                R: '#white-rook',
                B: '#white-bishop',
                N: '#white-knight',
                P: '#white-pawn',
            }

            var top = playerColor === white ? 7 - field.row : field.row;
            var topPos = top * fieldSize;

            var left = playerColor === white ? field.column : 7 - field.column;
            var leftPos = left * fieldSize;
            var newEl = $(document.createElement('div'));
            var attrs = {
                style: 'width: ' + fieldSize + 'px; height: ' + fieldSize + 'px; top: ' + topPos + 'px; left:' + leftPos + 'px; position: absolute',
                id: 'droppable_' + field.toString()
            }
            newEl.attr(attrs);

            if(piece !== undefined){
                var pieceElement = $(pieces[piece.toString()]);
                var srcForPiece = pieceElement.attr("src");
                var marginTop = Math.floor((fieldSize - pieceElement.height())/2);
                var pieceTopPos = topPos + marginTop;
                var marginLeft = Math.floor((fieldSize - pieceElement.width())/2);
                var pieceLeftPos = leftPos + marginLeft;
                newEl.html('<img src="' + srcForPiece + '" draggable="true" id="' + field.toString() + '" style="position: absolute; top:' + marginTop + 'px; left:' + marginLeft + 'px;" />');
            }

            newEl.bind('drop', drop);
            newEl.bind('dragover', dragover);
            $("#pieces-layer").append(newEl);

            if(piece !== undefined)
                $("#pieces-layer").find("#" + field.toString()).bind('dragstart', drag);
        }


        var game = new Game.ChessGame()
        var allFields = game.chessboard.getAllFields()
        _.each(allFields, function(field){
            var piece = game.chessboard.getPiece(field);
            drawPiece(piece, field);
        });

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
