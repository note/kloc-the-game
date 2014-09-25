"use strict"

require(['jquery', 'cookie', 'game', 'underscore', 'sprintf'], function($, __notUsed, Game, _, SprintfModule) {
    var sprintf = SprintfModule.sprintf;
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
    }

    ChessGameWebSocket.prototype.getReceiveMessage = function(){
        var that = this;
        return function(msg) {


            console.log('receivemessage');
            console.log(msg.data);
            var message = JSON.parse(msg.data);

            switch(message.type){
                case "start":
                    console.log('start message arrived');
                    $(that).trigger("/start", [message]);
                    break;
                case "move":
                    var times = message.times;
                    for(var playerName in times){
                        console.log(playerName + ": " + formatTime(times[playerName]));
                    }

                    if(message.userId === $.cookie('userId')){
                        $(that).trigger('/ownMove', [message]);
                    }else{
                        $(that).trigger('/move', [message]);
                    }
                    break;
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

    var refreshTimeMs = 1000;
    function WebGame(socket, drawer){
        this.game = new Game.ChessGame();
        this.socket = socket;
        this.drawer = drawer;

        this.bottomPlayerId = "#bottom_player";
        this.bottomTimeId = "#bottom_time";
        this.topPlayerId = "#top_player";
        this.topTimeId = "#top_time";

        this.playerNamesToColor = {};
        this.colorToPlayerNames = {};
        this.times = {};
        this.refreshTimeTask = null;

        $(socket).on("/start", this, this.onStartMessage);
        $(socket).on("/move", this, this.onMoveMessage);
        $(socket).on("/ownMove", this, this.onOwnMoveMessage);
        this.game.chessboard.addObserver(drawer);

        this.drawer.drawChessboard();
        var allFields = this.game.chessboard.getAllFields()
        _.each(allFields, function(field){
            var piece = this.game.chessboard.getPiece(field);
            this.drawer.drawField(field, piece, this);
        }, this);
    }

    function formatTime(ms){
        var totalSeconds = Math.floor(ms/1000);
        var seconds = totalSeconds % 60;
        var totalMinutes = Math.floor(totalSeconds/60);
        var minutes = totalMinutes % 60;
        var totalHours = Math.floor(totalMinutes/60);
        var res;
        if(totalHours > 0){
            res = sprintf("%d:%02d:%02d", totalHours, minutes, seconds);
        }else{
            res = sprintf("%02d:%02d", minutes, seconds);
        }
        return res;
    }

    WebGame.prototype.playerNameEl = function(color){
        return color === this.drawer.color ? $(this.bottomPlayerId) : $(this.topPlayerId);
    }

    WebGame.prototype.playerTimeEl = function(color){
        return color === this.drawer.color ? $(this.bottomTimeId) : $(this.topTimeId);
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
                clearInterval(that.refreshTimeTask);
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

    WebGame.prototype.onStartMessage = function(event, startMessage) {
        var that = event.data;
        for (var playerName in startMessage.colors){
            var color = Game.Color.fromString(startMessage.colors[playerName]);
            that.playerNameEl(color).html(playerName);
            that.playerNamesToColor [playerName]= color;
            that.colorToPlayerNames [color]= playerName;
        }
//        for (var playerName in startMessage.colors){
//            var color = Game.Color.fromString(startMessage.colors[playerName]);
//            that.playerTimeEl(color).html(formatTime(startMessage.times[playerName]));
//        }
        that.updateTimes(startMessage.times);
        that.refreshHud();
    }

    WebGame.prototype.onMoveMessage = function(event, moveMessage) {
        var that = event.data;
        function moveFromMsg(message) {
            var from = new Game.Field(message.from);
            var to = new Game.Field(message.to)
            return new Game.Move(from, to);
        }

        var move = moveFromMsg(moveMessage);
        that.game.applyMove(move);
        that.updateTimes(moveMessage.times);
        that.refreshHud();
    }

    WebGame.prototype.onOwnMoveMessage = function (event, moveMessage) {
        var that = event.data;
        that.updateTimes(moveMessage.times);
        that.refreshHud();
    }

    WebGame.prototype.updateTimes = function(times){
        this.times = times;
    };

    WebGame.prototype.getRefreshTimeFn = function(color){
        var that = this;
        return function(){
            var playerName = that.colorToPlayerNames[color];
            var lastTime = that.times[playerName];
            that.times [playerName]= lastTime - refreshTimeMs;
            var content = formatTime(that.times [playerName]);
            that.playerTimeEl(color).html(content);
        };
    };

    WebGame.prototype.refreshHud = function () {
        clearInterval(this.refreshTimeTask);
        for(var playerName in this.times){
            var color = this.playerNamesToColor[playerName];
            this.playerTimeEl(color).html(formatTime(this.times[playerName]));
        }
        this.refreshTimeTask = setInterval(this.getRefreshTimeFn(this.game.nextMoveColor), refreshTimeMs);
    };

    WebGame.prototype.sendMove = function (move) {
        var moveMessage = {type: "move", from: move.from.toString(), to: move.to.toString()};
        this.socket.send(JSON.stringify(moveMessage));
    };

    //// -----------------------------------
    function Drawer(perspectiveColor, fieldSize, scale, piecesLayer, chessboardCanvas){
        this.color = perspectiveColor;
        this.fieldSize = fieldSize;
        this.piecesLayer = piecesLayer;
        this.chessboardCanvas = chessboardCanvas;
        this.scale = scale;

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
            var height = Math.floor(pieceElement.height() * this.scale);
            var width = Math.floor(pieceElement.width() * this.scale);
            var marginTop = Math.floor((this.fieldSize - height)/2);
            var marginLeft = Math.floor((this.fieldSize - width)/2);
            var imgAttrs = {
                src: srcForPiece,
                draggable: true,
                style: "position: absolute; top: " + marginTop + "px; left: " + marginLeft + "px;",
                width: width
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
            var height = Math.floor(pieceElement.height() * this.scale);
            var width = Math.floor(pieceElement.width() * this.scale);
            var marginTop = Math.floor((this.fieldSize - height)/2);
            var marginLeft = Math.floor((this.fieldSize - width)/2);
            var imgAttrs = {
                src: srcForPiece,
                draggable: true,
                style: "position: absolute; top: " + marginTop + "px; left: " + marginLeft + "px;",
                width: width
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

//        var drawer = new Drawer(white, 70, 0.7, $("#pieces-layer"), $("#chessboard-canvas"));
//        var webGame = new WebGame({addObserver: function(a){}}, drawer);
//        $('#game-panel').show();

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
                var drawer = new Drawer(color, 70, 0.7, $("#pieces-layer"), $("#chessboard-canvas"));
                var webGame = new WebGame(socket, drawer);
                $('#game-panel').show();
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
