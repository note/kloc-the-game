define(['jquery', 'color'], function($, Color){
    var white = Color.white;

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

    function drag (event) {
        var draggedEl = event.originalEvent.target;
        var parentId = $(draggedEl).parent().attr("id");
        event.originalEvent.dataTransfer.setData("from", parentId);
        console.log("ondrag: " + parentId);
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
            img.bind('dragstart', drag);
            newEl.append(img);
//            newEl.html('<img src="' + srcForPiece + '" draggable="true" style="position: absolute; top:' + marginTop + 'px; left:' + marginLeft + 'px;" />');
        }

        newEl.bind('drop', webGame.getDropFn(newEl));
        newEl.bind('dragover', webGame.dragover);
        this.piecesLayer.append(newEl);

        if(piece !== undefined)
            this.piecesLayer.find("#" + field.toString()).bind('dragstart', drag);
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
            img.bind('dragstart', drag);
            fieldEl.html(img);
        }else{
            fieldEl.html("");
        }
    };

    return Drawer;
});
