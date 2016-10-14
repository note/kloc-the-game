@()(implicit request: RequestHeader)

"use strict"

require(['jquery', 'cookie', 'game', 'underscore', 'sprintf', 'drawer', 'backbone', 'vex', 'vexDialog'], function($, __notUsed, Game, _, SprintfModule, Drawer, Backbone, vex, VexDialog) {
    var sprintf = SprintfModule.sprintf;
    var registerUserUrl = '@routes.UserController.logInUser()';
    var loggedInUrl = '@routes.UserController.isUserLoggedIn()';
    var listRoomsUrl = '@routes.RoomController.listRooms().webSocketURL()';
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket;
    vex.defaultOptions.className = 'vex-theme-os';
    vex.dialog = VexDialog;

    var white = Game.Color.white;
    var black = Game.Color.black;

    function getJoinRoomURL(roomId){
        return jsRoutes.controllers.RoomController.joinRoom(roomId).webSocketURL();
    }

    function getCreateRoomURL(timeLimitInSeconds){
        return jsRoutes.controllers.RoomController.createRoom(timeLimitInSeconds).url;
    }

    var TableModel = Backbone.Model.extend({
        defaults: {
            white: null,
            black: null,
            url: null
        }
    });

    var TableCollection = Backbone.Collection.extend({
        model: TableModel
    });

    var RoomModel = Backbone.Model.extend({
        fromArray: function(roomId, tables){
            _.each(tables, function(table){
                table.url = getJoinRoomURL(roomId);
            });

            var tableCollection = new TableCollection(tables);
            this.set('tablesCollection', tableCollection);
            this.set('roomId', roomId);
        }
    });

    var RoomCollection = Backbone.Collection.extend({
        model: RoomModel,

        fromMessage: function(msg){
            var data = JSON.parse(msg.data);
            var rooms = [];
            for(var roomId in data.rooms){
                var roomModel = new RoomModel();
                roomModel.fromArray(roomId, data.rooms[roomId]);
                rooms.push(roomModel);
            }
            this.reset(rooms);
        }
    });

    var roomsCollection = new RoomCollection();

    var RoomsView = Backbone.View.extend({
        el: '#rooms-list',

        render: function() {
            this.$el.html('');

            roomsCollection.forEach(function(room){
                var roomView = new RoomView({model: room});
                this.$el.append(roomView.render().el);
            }, this);
            return this;
        },

        initialize: function() {
            this.listenTo(roomsCollection, 'reset', this.render);
        }

    });

    var RoomView = Backbone.View.extend({
        tagName: 'li',

        render: function() {
            var roomModel = this.model;
            roomModel.get('tablesCollection').forEach(function(table){
                var tableView  = new TableView({model: table});
                this.$el.append(tableView.render().el);
            }, this);
            return this;
        }
    });

    var TableView = Backbone.View.extend({
        template: _.template($('#table-template').html()),

        render: function() {
            this.$el.html(this.template(this.model.attributes));
            return this;
        }
    });

    var roomsView = new RoomsView();

    function RoomsWebsocket(url){
        this.ws = new WS(url);
        this.ws.onmessage = this.onmessage;
        this.ws.onclose = this.onclose;
        this.ws.onerror = this.onerror;
        this.ws.onopen = this.onopen;
    }

    RoomsWebsocket.prototype.onopen = function(){
        console.log("listrooms open")
    }

    RoomsWebsocket.prototype.onmessage = function(msg){
        console.log("onmessage listrooms: " + msg.data);
        roomsCollection.fromMessage(msg);
    };

    RoomsWebsocket.prototype.onclose = function(){

    };

    RoomsWebsocket.prototype.onerror = function(){

    };

    function ChessGameWebSocket(url, color){
        var params = {color: color.toString()};
        var fullUrl = [url, "&", $.param(params)].join("");
        this.ws = new WS(fullUrl);

        this.ws.onmessage = this.getReceiveMessage();
        this.ws.onclose = this.onclose;
        this.ws.onerror = this.onerror;
        this.ws.onopen = this.getOnOpenFun(color);
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
                    $(that.ws).trigger("/start", [message]);
                    break;
                case "move":
                    var times = message.times;
                    for(var playerName in times){
                        console.log(playerName + ": " + formatTime(times[playerName]));
                    }

                    if(message.userId === $.cookie('userId')){
                        $(that.ws).trigger('/ownMove', [message]);
                    }else{
                        $(that.ws).trigger('/move', [message]);
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

    ChessGameWebSocket.prototype.getOnOpenFun = function(color) {
        var that = this;
        return function() {
            console.log("onopen game");
            $('#game-panel').show();
        };
    };

    var refreshTimeMs = 1000;
    function WebGame(socket, drawer){
        this.game = new Game.ChessGame();
        this.socket = socket;
        this.drawer = drawer;

        this.bottomPlayerId = "#bottom-player";
        this.bottomTimeId = "#bottom-time";
        this.topPlayerId = "#top-player";
        this.topTimeId = "#top-time";

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

    WebGame.prototype.addPlayer = function(playerName, color){
        this.playerNameEl(color).html(playerName);
        this.playerNamesToColor [playerName]= color;
        this.colorToPlayerNames [color]= playerName;
    };

    WebGame.prototype.onStartMessage = function(event, startMessage) {
        var that = event.data;
        for (var playerName in startMessage.colors){
            var color = Game.Color.fromString(startMessage.colors[playerName]);
            that.addPlayer(playerName, color);
        }
        that.updateTimes(startMessage.times);
        that.refreshHud();
    };

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
    };

    WebGame.prototype.onOwnMoveMessage = function (event, moveMessage) {
        var that = event.data;
        that.updateTimes(moveMessage.times);
        that.refreshHud();
    };

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
    function loggedIn(userId){
        var loggedInPromise = new Promise(function(resolve, reject){
            $.get(loggedInUrl, {userId: userId}, function(data){
                if(data.result === true){
                    resolve();
                }else{
                    reject();
                }
            });
        });
        return loggedInPromise;
    }

    function activateRoomsPanel(){
        $("#rooms-panel").show();
        var socket = new RoomsWebsocket(listRoomsUrl);
    }

    $(document).ready(function() {
        var userId = $.cookie("userId");

        var notLoggedFn = function() {
            $("#login-panel").show();
        };

        if(userId){
            loggedIn(userId).then(
                function() {
                    activateRoomsPanel();
                },
                notLoggedFn
            );
        }else{
            notLoggedFn();
        }

        $("#send-introduce").submit(function(ev){
            ev.preventDefault();

            var userName = $("#user-name").val();
            $.get(registerUserUrl, {userName: userName}, function(data){
                console.log(data);
                if(data && data.userId){
                    $.cookie("userId", data.userId);
                    $.cookie("userName", userName);
                    $("#login-panel").hide();
                    activateRoomsPanel();
                }
            });
        });

        $('#create-room').click(function(){
            vex.dialog.open({
                message: 'Choose color:',
                input: '<p><input type="radio" name="color" id="white-color-dialog" value="w" checked /><label for="white-color-dialog">White</label></p>' +
                       '<p><input type="radio" name="color" id="black-color-dialog" value="b" /><label for="black-color-dialog">Black</label></p>' +
                       '<p><label for="time-limit-dialog">Time limit: (in minutes)</label><input type="text" name="time-limit" id="time-limit-dialog" value="5" /></p>',
                callback: function(dialogData) {
                    console.log(dialogData);
                    var color = Game.Color.fromString(dialogData.color);
                    var timeLimitInSeconds = dialogData['time-limit'] * 60;
                    $.get(getCreateRoomURL(timeLimitInSeconds), function(data){
                        if(data && data.roomId !== undefined){
                            $("#rooms-panel").hide();
                            var url = getJoinRoomURL(data.roomId);
                            var socket = new ChessGameWebSocket(url, color);
                            var drawer = new Drawer(color, 70, 0.7, $("#pieces-layer"), $("#chessboard-canvas"));
                            var webGame = new WebGame(socket.ws, drawer);
                            var userName = $.cookie('userName');
                            webGame.addPlayer(userName);
                            webGame.playerTimeEl(white).html(formatTime(timeLimitInSeconds * 1000));
                            webGame.playerTimeEl(black).html(formatTime(timeLimitInSeconds * 1000));
                        }
                    });
                }
            });
        });

        $('body').on('click', '.join-room', function(){
            $("#rooms-panel").hide();
            var url = $(this).attr("href");
            var color = $(this).hasClass("white") ? white : black;
            var socket = new ChessGameWebSocket(url, color);
            var drawer = new Drawer(color, 70, 0.7, $("#pieces-layer"), $("#chessboard-canvas"));
            var webGame = new WebGame(socket.ws, drawer);
        });
    });

});
