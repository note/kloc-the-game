"use strict"

require(['jquery', 'cookie', 'game', 'underscore'], function($, __notUsed, Game, _) {
    var registerUserUrl = '@routes.Application.registerUser()';
    var createRoomUrl = '@routes.Application.createRoom()';
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket

    var white = Game.Color.white;
    var black = Game.Color.black;

    function ChessGameWebSocket(url){
        this.ws = new WS(url);
        this.ws.onmessage = this.getReceiveMessage();
        this.ws.onclose = this.onclose;
        this.ws.onerror = this.onerror;
        this.observers = [];
    }

    ChessGameWebSocket.prototype.getReceiveMessage = function(){
        var that = this;
        return function(msg) {
            function moveFromMsg(message) {
                var from = new Game.Field(message.from);
                var to = new Game.Field(message.to)
                return new Game.Move(from, to);
            }

            console.log('receivemessage');
            console.log(msg.data);
            var message = JSON.parse(msg.data);

            if(message.sender !== $.cookie('userId')){
                var move = moveFromMsg(message);
                that.notify(move);
            }
        };
    };

    ChessGameWebSocket.prototype.send = function(msg){
        this.ws.send(msg);
    }

    ChessGameWebSocket.prototype.onclose = function () {
        console.log("websocket closed");
    };

    ChessGameWebSocket.prototype.onerror = function () {
        console.log("websocket onerror");
    };

    ChessGameWebSocket.prototype.addObserver = function (observer) {
        this.observers.push(observer);
    };

    ChessGameWebSocket.prototype.notify = function(move) {
        _.each(this.observers, function(observer){
            observer.update(move);
        });
    };

    function WebGame(socket, drawer){
        this.game = new Game.ChessGame();
        this.socket = socket;
        this.drawer = drawer;

        this.socket.addObserver(this);
        this.game.chessboard.addObserver(drawer);

        this.drawer.drawChessboard();
        var allFields = this.game.chessboard.getAllFields()
        _.each(allFields, function(field){
            var piece = this.game.chessboard.getPiece(field);
            this.drawer.drawField(field, piece, this);
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

    WebGame.drag = function (event) {
        var draggedEl = event.originalEvent.target;
        var parentId = $(draggedEl).parent().attr("id");
        event.originalEvent.dataTransfer.setData("from", parentId);
        console.log("ondrag: " + parentId);
    }

    WebGame.prototype.getDropFn = function (field) {
        var that = this;

        return function(event) {
            event.preventDefault();
            console.log("ondrop");

            var from = event.originalEvent.dataTransfer.getData("from");
            var to = field.attr("id");
            var move = new Game.Move(new Game.Field(from), new Game.Field(to));
            if(that.game.isLegalMove(move) && that.game.nextMoveColor === that.drawer.color){
                that.game.applyMove(move);
                that.sendMove(move);
            }
        };
    }

    WebGame.prototype.dragover = function (event) {
        console.log("ondragover");
        var dt = event.originalEvent.dataTransfer;
        dt.dropEffect = "move";
        event.preventDefault();
    }

    WebGame.prototype.update = function(move) {
        this.game.applyMove(move);
    }

    WebGame.prototype.sendMove = function (move) {
        var moveMessage = {type: "move", from: move.from.toString(), to: move.to.toString()};
        this.socket.send(JSON.stringify(moveMessage));
    }

    Drawer.prototype.drawField = function(field, piece, webGame) {
        var topPos = this.getRow(field) * this.fieldSize;
        var leftPos = this.getColumn(field) * this.fieldSize;
        var newEl = $(document.createElement('div'));
        var attrs = {
            style: 'width: ' + this.fieldSize + 'px; height: ' + this.fieldSize + 'px; top: ' + topPos + 'px; left:' + leftPos + 'px; position: absolute',
            id: field.toString()
        }
        newEl.attr(attrs);

        if(piece !== undefined){
            var pieceElement = $(this.piecesToElements[piece.toString()]);
            var srcForPiece = pieceElement.attr("src");
            var marginTop = Math.floor((this.fieldSize - pieceElement.height())/2);
            var marginLeft = Math.floor((this.fieldSize - pieceElement.width())/2);
            var imgAttrs = {
                src: srcForPiece,
                draggable: true,
                style: "position: absolute; top: " + marginTop + "px; left: " + marginLeft + "px;"
            };
            var img = $(document.createElement('img'));
            img.attr(imgAttrs);
            img.bind('dragstart', WebGame.drag);
            newEl.append(img);
//            newEl.html('<img src="' + srcForPiece + '" draggable="true" style="position: absolute; top:' + marginTop + 'px; left:' + marginLeft + 'px;" />');
        }

        newEl.bind('drop', webGame.getDropFn(newEl));
        newEl.bind('dragover', webGame.dragover);
        this.piecesLayer.append(newEl);

        if(piece !== undefined)
            this.piecesLayer.find("#" + field.toString()).bind('dragstart', webGame.drag);
    };

    Drawer.prototype.drawPiece = function(field, piece) {
        var fieldEl = $('#' + field.toString());

        // TODO: code duplicated with drawField
        if(piece !== undefined){
            var pieceElement = $(this.piecesToElements[piece.toString()]);
            var srcForPiece = pieceElement.attr("src");
            var marginTop = Math.floor((this.fieldSize - pieceElement.height())/2);
            var marginLeft = Math.floor((this.fieldSize - pieceElement.width())/2);
            var imgAttrs = {
                src: srcForPiece,
                draggable: true,
                style: "position: absolute; top: " + marginTop + "px; left: " + marginLeft + "px;"
            };
            var img = $(document.createElement('img'));
            img.attr(imgAttrs);
            img.bind('dragstart', WebGame.drag);
            fieldEl.html(img);
        }else{
            fieldEl.html("");
        }
    };

    $(document).ready(function() {

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
            var socket = new ChessGameWebSocket(url);

            socket.ws.onopen = function(){
                console.log("onopen");
                var color = $(that).hasClass("white") ? white : black;
                var drawer = new Drawer(color, 100, $("#pieces-layer"), $("#chessboard-canvas"));
                var webGame = new WebGame(socket, drawer);
                $('#table').show();
            };

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
