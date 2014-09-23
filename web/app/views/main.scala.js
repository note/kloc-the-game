"use strict"

require(['jquery', 'cookie'], function($) {
    var registerUserUrl = '@routes.Application.registerUser()';
    var createRoomUrl = '@routes.Application.createRoom()';
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket

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
            $("#game-panel").show();
            $("#send-move").click(function(){
                var fromStr = $("#from").val();
                var toStr = $("#to").val();
                var moveMessage = {type: "move", from: fromStr, to: toStr};
                socket.send(JSON.stringify(moveMessage));
            });
        });
    });


//    var sendMessage = function() {
//        chatSocket.send(JSON.stringify(
//            {text: $("#talk").val()}
//        ))
//        $("#talk").val('')
//    }
//
//    var receiveEvent = function(event) {
//        var data = JSON.parse(event.data)
//
//        // Handle errors
//        if(data.error) {
//            chatSocket.close()
//            $("#onError span").text(data.error)
//            $("#onError").show()
//            return
//        } else {
//            $("#onChat").show()
//        }
//
//        // Create the message element
//        var el = $('<div class="message"><span></span><p></p></div>')
//        $("span", el).text(data.user)
//        $("p", el).text(data.message)
//        $(el).addClass(data.kind)
//        $('#messages').append(el)
//
//        // Update the members list
//        $("#members").html('')
//        $(data.members).each(function() {
//            var li = document.createElement('li');
//            li.textContent = this;
//            $("#members").append(li);
//        })
//    }
//
//    var handleReturnKey = function(e) {
//        if(e.charCode == 13 || e.keyCode == 13) {
//            e.preventDefault()
//            sendMessage()
//        }
//    }
//
//    $("#talk").keypress(handleReturnKey)
//
//    chatSocket.onmessage = receiveEvent
});
