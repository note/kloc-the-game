"use strict"

require(['jquery'], function($) {
    var createTableURL = '@routes.Application.createTable()';
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket

    function createGameWebsocket(url){
        var socket = new WS(url);

        var receiveMessage = function(msg){
            console.log(msg);
            console.log(msg.data);
        };

        socket.onmessage = receiveMessage;
        return socket;
    }

    $(document).ready(function() {
        console.log("ready!");

        $('#create-table').click(function(){
            var tableName = $('#new-table-name').val();
            $.get(createTableURL, {name: tableName}, function(data){
                console.log(data);
                var wsUrl = data.url;
                createGameWebsocket(wsUrl);
            });
        });

        $(".join-table").click(function(){
            var url = $(this).attr("href");
            var socket = createGameWebsocket(url);
            $("#game-panel").show();
            $("#send-move").click(function(){
                var fromStr = $("#from").val();
                var toStr = $("#to").val();
                var moveMessage = {player: "player1", "from": fromStr, "to": toStr};
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
