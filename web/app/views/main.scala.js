"use strict"

require(['jquery', 'cookie', 'game', 'underscore'], function($, __notUsed, Game, _) {
    var registerUserUrl = '@routes.Application.registerUser()';
    var createRoomUrl = '@routes.Application.createRoom()';
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket

    var white = Game.Color.white;

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

    function WebGame(socket, drawer){
        this.game = new Game.ChessGame()
        this.socket = socket;
        this.drawer = drawer;
        this.game.chessboard.addObserver(drawer);

        this.drawer.drawChessboard();
        var allFields = this.game.chessboard.getAllFields()
        _.each(allFields, function(field){
            var piece = this.game.chessboard.getPiece(field);
            this.drawer.drawPiece(field, piece);
        }, this);
    }

    function Drawer(perspectiveColor, fieldSize, piecesLayer, chessboardCanvas){
        this.color = perspectiveColor;
        this.fieldSize = fieldSize;
        this.piecesLayer = piecesLayer;
        this.chessboardCanvas = chessboardCanvas;

        this.piecesToElements = {
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
    }

    Drawer.prototype.drawChessboard = function(){
        var canvas = this.chessboardCanvas.get(0);
        var chessboardContext = canvas.getContext('2d');
        for(var row = 0; row < 8; ++row){
            for(var column = 0; column < 8; ++column){
                chessboardContext.beginPath();
                chessboardContext.rect(column * this.fieldSize, row * this.fieldSize, this.fieldSize, this.fieldSize);
                chessboardContext.fillStyle = column % 2 == row % 2 ? '#fff' : '#a1a1a1';
                chessboardContext.fill();
            }
        }
    };

    Drawer.prototype.update = function(field, piece) {
        this.drawPiece(field, piece);
    }

    Drawer.prototype.getRow = function(field) {
        return this.color === white ? 7 - field.row : field.row;
    }

    Drawer.prototype.getColumn = function(field) {
        return this.color === white ? field.column : 7 - field.column;
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

    Drawer.prototype.drawPiece = function(field, piece) {
        var topPos = this.getRow(field) * this.fieldSize;
        var leftPos = this.getColumn(field) * this.fieldSize;
        var newEl = $(document.createElement('div'));
        var attrs = {
            style: 'width: ' + this.fieldSize + 'px; height: ' + this.fieldSize + 'px; top: ' + topPos + 'px; left:' + leftPos + 'px; position: absolute',
            id: 'droppable_' + field.toString()
        }
        newEl.attr(attrs);

        if(piece !== undefined){
            var pieceElement = $(this.piecesToElements[piece.toString()]);
            var srcForPiece = pieceElement.attr("src");
            var marginTop = Math.floor((this.fieldSize - pieceElement.height())/2);
            var marginLeft = Math.floor((this.fieldSize - pieceElement.width())/2);
            newEl.html('<img src="' + srcForPiece + '" draggable="true" id="' + field.toString() + '" style="position: absolute; top:' + marginTop + 'px; left:' + marginLeft + 'px;" />');
        }

        newEl.bind('drop', drop);
        newEl.bind('dragover', dragover);
        this.piecesLayer.append(newEl);

        if(piece !== undefined)
            this.piecesLayer.find("#" + field.toString()).bind('dragstart', drag);
    };

    $(document).ready(function() {

        var drawer = new Drawer(white, 100, $("#pieces-layer"), $("#chessboard-canvas"));
        var webGame = new WebGame(null, drawer);

        $("#send-introduce").click(function(){
            var playerName = $("#player-name").val();
            $.get(registerUserUrl, {"playerName": playerName}, function(data){
                console.log(data);
                $.cookie("userId", data.userId);
            });
        });

        $('#create-room').click(function(){
            var roomName = $('#new-room-name').val();
            $.get(createRoomUrl, {name: roomName}, function(data){
                console.log(data);
//                var wsUrl = data.url;
//                createGameWebsocket(wsUrl);
            });
        });

        $(".join-room").click(function(){
            var that = this;

            function createUrl(baseUrl) {
                var params = {
                    color: $(that).hasClass("white") ? "w" : "b"
                };
                return [baseUrl, "&", $.param(params)].join("");
            }

            var url = createUrl($(this).attr("href"));
            var socket = createGameWebsocket(url);
            new WebGame(socket);
            new Drawer(white, 100, $("#pieces-layer"), $("#chessboard-canvas"));
//            $("#game-panel").show();
//            $("#send-move").click(function(){
//                var fromStr = $("#from").val();
//                var toStr = $("#to").val();
//                var moveMessage = {type: "move", from: fromStr, to: toStr};
//                socket.send(JSON.stringify(moveMessage));
//            });
        });
    });

});
